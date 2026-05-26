from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import db
from models import Transaction, Offer, Voucher, Dispute, User
from uuid import UUID
from datetime import datetime

transactions_bp = Blueprint('transactions', __name__, url_prefix='/api/v1')


def _txn_dict(t):
    return {
        'id': str(t.id),
        'offer_id': str(t.offer_id),
        'buyer_id': str(t.buyer_id),
        'vendor_id': str(t.vendor_id),
        'amount_crypto': t.amount_crypto,
        'amount_fiat': t.amount_fiat,
        'exchange_rate': t.exchange_rate,
        'status': t.status,
        'buyer_payment_account': t.buyer_payment_account,
        'vendor_payment_account': t.vendor_payment_account,
        'created_at': t.created_at.isoformat(),
        'updated_at': t.updated_at.isoformat() if t.updated_at else None
    }


@transactions_bp.route('/transactions', methods=['GET'])
@jwt_required()
def list_transactions():
    user_id = get_jwt_identity()
    uid = UUID(user_id)

    status_filter = request.args.get('status')
    query = Transaction.query.filter(
        (Transaction.buyer_id == uid) | (Transaction.vendor_id == uid)
    )
    if status_filter:
        query = query.filter_by(status=status_filter)

    txns = query.order_by(Transaction.created_at.desc()).all()
    return {'transactions': [_txn_dict(t) for t in txns]}, 200


@transactions_bp.route('/transactions/pending', methods=['GET'])
@jwt_required()
def pending_transactions():
    """Vendor inbox — transactions waiting for vendor action"""
    user_id = get_jwt_identity()
    txns = Transaction.query.filter(
        Transaction.vendor_id == UUID(user_id),
        Transaction.status.in_(('pending', 'voucher_uploaded'))
    ).order_by(Transaction.created_at.desc()).all()
    return {'transactions': [_txn_dict(t) for t in txns]}, 200


@transactions_bp.route('/transactions/<txn_id>', methods=['GET'])
@jwt_required()
def get_transaction(txn_id):
    user_id = get_jwt_identity()
    try:
        txn = Transaction.query.filter_by(id=UUID(txn_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid transaction ID'}}, 400

    if not txn:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Transaction not found'}}, 404
    if str(txn.buyer_id) != user_id and str(txn.vendor_id) != user_id:
        return {'error': {'code': 'FORBIDDEN', 'message': 'Not your transaction'}}, 403

    return _txn_dict(txn), 200


@transactions_bp.route('/transactions', methods=['POST'])
@jwt_required()
def create_transaction():
    user_id = get_jwt_identity()
    data = request.get_json()

    try:
        offer = Offer.query.filter_by(id=UUID(data.get('offer_id'))).first()
    except Exception:
        return {'error': {'code': 'INVALID_OFFER', 'message': 'Invalid offer ID'}}, 400

    if not offer or offer.status != 'active':
        return {'error': {'code': 'OFFER_UNAVAILABLE', 'message': 'Offer not available'}}, 400

    if str(offer.vendor_id) == user_id:
        return {'error': {'code': 'OWN_OFFER', 'message': 'Cannot buy your own offer'}}, 400

    txn = Transaction(
        offer_id=offer.id,
        buyer_id=UUID(user_id),
        vendor_id=offer.vendor_id,
        amount_crypto=data.get('amount_crypto', 0),
        amount_fiat=data.get('amount_fiat', 0),
        exchange_rate=offer.price_per_unit,
        status='pending',
        buyer_payment_account=data.get('buyer_payment_account'),
        vendor_payment_account=data.get('vendor_payment_account')
    )

    db.session.add(txn)
    db.session.commit()
    return _txn_dict(txn), 201


@transactions_bp.route('/transactions/<txn_id>/voucher', methods=['POST'])
@jwt_required()
def upload_voucher(txn_id):
    user_id = get_jwt_identity()
    try:
        txn = Transaction.query.filter_by(id=UUID(txn_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid transaction ID'}}, 400

    if not txn:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Transaction not found'}}, 404
    if str(txn.buyer_id) != user_id:
        return {'error': {'code': 'FORBIDDEN', 'message': 'Only buyer can upload voucher'}}, 403

    data = request.get_json()
    voucher = Voucher(
        transaction_id=txn.id,
        sender_id=UUID(user_id),
        image_url=data.get('image_url', ''),
        description=data.get('description'),
        status='pending'
    )
    db.session.add(voucher)

    txn.status = 'voucher_uploaded'
    txn.updated_at = datetime.utcnow()
    db.session.commit()

    return {'id': str(voucher.id), 'status': 'pending', 'transaction_status': 'voucher_uploaded'}, 201


@transactions_bp.route('/transactions/<txn_id>/confirm', methods=['POST'])
@jwt_required()
def confirm_transaction(txn_id):
    """Vendor confirms payment received — releases funds"""
    user_id = get_jwt_identity()
    try:
        txn = Transaction.query.filter_by(id=UUID(txn_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid transaction ID'}}, 400

    if not txn:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Transaction not found'}}, 404
    if str(txn.vendor_id) != user_id:
        return {'error': {'code': 'FORBIDDEN', 'message': 'Only vendor can confirm'}}, 403
    if txn.status not in ('voucher_uploaded', 'pending'):
        return {'error': {'code': 'INVALID_STATE', 'message': f'Cannot confirm from status {txn.status}'}}, 400

    txn.status = 'completed'
    txn.updated_at = datetime.utcnow()

    # Increase vendor total_transactions
    vendor = User.query.get(txn.vendor_id)
    buyer = User.query.get(txn.buyer_id)
    if vendor:
        vendor.total_transactions += 1
    if buyer:
        buyer.total_transactions += 1

    db.session.commit()
    return {'message': 'Transaction confirmed and completed', 'status': 'completed'}, 200


@transactions_bp.route('/transactions/<txn_id>/dispute', methods=['POST'])
@jwt_required()
def open_dispute(txn_id):
    user_id = get_jwt_identity()
    try:
        txn = Transaction.query.filter_by(id=UUID(txn_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid transaction ID'}}, 400

    if not txn:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Transaction not found'}}, 404
    if str(txn.buyer_id) != user_id and str(txn.vendor_id) != user_id:
        return {'error': {'code': 'FORBIDDEN', 'message': 'Not your transaction'}}, 403
    if txn.status == 'completed':
        return {'error': {'code': 'INVALID_STATE', 'message': 'Cannot dispute a completed transaction'}}, 400

    data = request.get_json() or {}
    dispute = Dispute(
        transaction_id=txn.id,
        initiator_id=UUID(user_id),
        reason=data.get('reason', 'No reason provided'),
        description=data.get('description'),
        status='open'
    )
    db.session.add(dispute)

    txn.status = 'disputed'
    txn.updated_at = datetime.utcnow()
    db.session.commit()

    return {'id': str(dispute.id), 'status': 'open', 'transaction_status': 'disputed'}, 201


@transactions_bp.route('/transactions/<txn_id>/status', methods=['PATCH'])
@jwt_required()
def update_status(txn_id):
    user_id = get_jwt_identity()
    try:
        txn = Transaction.query.filter_by(id=UUID(txn_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid transaction ID'}}, 400

    if not txn:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Transaction not found'}}, 404
    if str(txn.buyer_id) != user_id and str(txn.vendor_id) != user_id:
        return {'error': {'code': 'FORBIDDEN', 'message': 'Not your transaction'}}, 403

    data = request.get_json() or {}
    allowed = ('cancelled', 'paused')
    new_status = data.get('status')
    if new_status not in allowed:
        return {'error': {'code': 'INVALID_STATUS', 'message': f'Status must be one of {allowed}'}}, 400

    txn.status = new_status
    txn.updated_at = datetime.utcnow()
    db.session.commit()
    return _txn_dict(txn), 200
