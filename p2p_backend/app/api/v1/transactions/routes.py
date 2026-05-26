"""Transactions routes — /api/v1/transactions/*"""
from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.core.database import db
from app.core.exceptions import NotFoundError, AuthorizationError, AppException
from app.models import Offer, Transaction, Voucher, Dispute
from app.models.user import User

transactions_bp = Blueprint('transactions', __name__, url_prefix='/transactions')


def _txn_dict(t):
    return {
        'id': t.id,
        'offer_id': t.offer_id,
        'buyer_id': t.buyer_id,
        'vendor_id': t.vendor_id,
        'amount_from': t.amount_from,
        'amount_to': t.amount_to,
        'exchange_rate': t.exchange_rate,
        'status': t.status,
        'buyer_payment_account': t.buyer_payment_account,
        'vendor_payment_account': t.vendor_payment_account,
        'created_at': t.created_at.isoformat(),
        'updated_at': t.updated_at.isoformat() if t.updated_at else None,
    }


@transactions_bp.route('', methods=['GET'])
@transactions_bp.route('/', methods=['GET'])
@jwt_required()
def list_transactions():
    user_id = get_jwt_identity()
    status_filter = request.args.get('status')

    query = Transaction.query.filter(
        (Transaction.buyer_id == user_id) | (Transaction.vendor_id == user_id)
    )
    if status_filter:
        query = query.filter_by(status=status_filter)

    txns = query.order_by(Transaction.created_at.desc()).all()
    return {'transactions': [_txn_dict(t) for t in txns]}, 200


@transactions_bp.route('/pending', methods=['GET'])
@jwt_required()
def pending_transactions():
    user_id = get_jwt_identity()
    txns = Transaction.query.filter(
        Transaction.vendor_id == user_id,
        Transaction.status.in_(('pending', 'voucher_uploaded'))
    ).order_by(Transaction.created_at.desc()).all()
    return {'transactions': [_txn_dict(t) for t in txns]}, 200


@transactions_bp.route('/<txn_id>', methods=['GET'])
@jwt_required()
def get_transaction(txn_id):
    user_id = get_jwt_identity()
    txn = db.session.get(Transaction, txn_id)
    if not txn:
        raise NotFoundError('Transaction not found')
    if txn.buyer_id != user_id and txn.vendor_id != user_id:
        raise AuthorizationError('Not your transaction')
    return _txn_dict(txn), 200


@transactions_bp.route('', methods=['POST'])
@transactions_bp.route('/', methods=['POST'])
@jwt_required()
def create_transaction():
    user_id = get_jwt_identity()
    data = request.get_json() or {}

    offer = db.session.get(Offer, data.get('offer_id'))
    if not offer or offer.status != 'active':
        raise AppException('OFFER_UNAVAILABLE', 'Offer not available', 400)
    if offer.vendor_id == user_id:
        raise AppException('OWN_OFFER', 'Cannot buy your own offer', 400)

    txn = Transaction(
        offer_id=offer.id,
        buyer_id=user_id,
        vendor_id=offer.vendor_id,
        amount_from=data.get('amount_from', 0),
        amount_to=data.get('amount_to', 0),
        exchange_rate=offer.price_per_unit,
        status='pending',
        buyer_payment_account=data.get('buyer_payment_account'),
        vendor_payment_account=data.get('vendor_payment_account'),
    )
    db.session.add(txn)
    db.session.commit()
    return _txn_dict(txn), 201


@transactions_bp.route('/<txn_id>/voucher', methods=['POST'])
@jwt_required()
def upload_voucher(txn_id):
    user_id = get_jwt_identity()
    txn = db.session.get(Transaction, txn_id)
    if not txn:
        raise NotFoundError('Transaction not found')
    if txn.buyer_id != user_id:
        raise AuthorizationError('Only buyer can upload voucher')

    data = request.get_json() or {}
    voucher = Voucher(
        transaction_id=txn.id,
        sender_id=user_id,
        image_url=data.get('image_url', ''),
        description=data.get('description'),
        status='pending',
    )
    db.session.add(voucher)
    txn.status = 'voucher_uploaded'
    db.session.commit()
    return {'id': voucher.id, 'status': 'pending', 'transaction_status': 'voucher_uploaded'}, 201


@transactions_bp.route('/<txn_id>/confirm', methods=['POST'])
@jwt_required()
def confirm_transaction(txn_id):
    user_id = get_jwt_identity()
    txn = db.session.get(Transaction, txn_id)
    if not txn:
        raise NotFoundError('Transaction not found')
    if txn.vendor_id != user_id:
        raise AuthorizationError('Only vendor can confirm')
    if txn.status not in ('voucher_uploaded', 'pending'):
        raise AppException('INVALID_STATE', f'Cannot confirm from {txn.status}', 400)

    txn.status = 'completed'
    vendor = db.session.get(User, txn.vendor_id)
    buyer  = db.session.get(User, txn.buyer_id)
    if vendor:
        vendor.total_transactions = (vendor.total_transactions or 0) + 1
    if buyer:
        buyer.total_transactions = (buyer.total_transactions or 0) + 1

    db.session.commit()
    return {'message': 'Transaction completed', 'status': 'completed'}, 200


@transactions_bp.route('/<txn_id>/dispute', methods=['POST'])
@jwt_required()
def open_dispute(txn_id):
    user_id = get_jwt_identity()
    txn = db.session.get(Transaction, txn_id)
    if not txn:
        raise NotFoundError('Transaction not found')
    if txn.buyer_id != user_id and txn.vendor_id != user_id:
        raise AuthorizationError('Not your transaction')
    if txn.status == 'completed':
        raise AppException('INVALID_STATE', 'Cannot dispute completed transaction', 400)

    data = request.get_json() or {}
    dispute = Dispute(
        transaction_id=txn.id,
        initiator_id=user_id,
        reason=data.get('reason', 'No reason provided'),
        description=data.get('description'),
        status='open',
    )
    db.session.add(dispute)
    txn.status = 'disputed'
    db.session.commit()
    return {'id': dispute.id, 'status': 'open', 'transaction_status': 'disputed'}, 201


@transactions_bp.route('/<txn_id>/status', methods=['PATCH'])
@jwt_required()
def update_status(txn_id):
    user_id = get_jwt_identity()
    txn = db.session.get(Transaction, txn_id)
    if not txn:
        raise NotFoundError('Transaction not found')
    if txn.buyer_id != user_id and txn.vendor_id != user_id:
        raise AuthorizationError('Not your transaction')

    data = request.get_json() or {}
    new_status = data.get('status')
    if new_status not in ('cancelled', 'paused'):
        raise AppException('INVALID_STATUS', 'Status must be cancelled or paused', 400)

    txn.status = new_status
    db.session.commit()
    return _txn_dict(txn), 200
