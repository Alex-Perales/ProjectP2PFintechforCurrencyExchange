from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import db
from models import User, Transaction, Dispute
from sqlalchemy import func
from uuid import UUID

admin_bp = Blueprint('admin', __name__, url_prefix='/api/v1/admin')


def _require_admin():
    user_id = get_jwt_identity()
    user = User.query.filter_by(id=UUID(user_id)).first()
    if not user or user.role != 'admin':
        return None, ({'error': {'code': 'FORBIDDEN', 'message': 'Admin only'}}, 403)
    return user, None


@admin_bp.route('/dashboard', methods=['GET'])
@jwt_required()
def dashboard():
    _, err = _require_admin()
    if err:
        return err

    total_users = User.query.count()
    total_txns = Transaction.query.count()
    pending_disputes = Dispute.query.filter_by(status='open').count()
    total_volume = db.session.query(
        func.sum(Transaction.amount_fiat)
    ).filter_by(status='completed').scalar() or 0

    return {
        'total_users': total_users,
        'total_transactions': total_txns,
        'pending_disputes': pending_disputes,
        'total_volume': float(total_volume)
    }, 200


@admin_bp.route('/users', methods=['GET'])
@jwt_required()
def list_users():
    _, err = _require_admin()
    if err:
        return err

    users = User.query.order_by(User.created_at.desc()).all()
    return {
        'users': [
            {
                'id': str(u.id),
                'email': u.email,
                'full_name': u.full_name,
                'role': u.role,
                'kyc_verified': u.kyc_verified,
                'rating': u.rating,
                'total_transactions': u.total_transactions,
                'is_active': u.is_active,
                'is_banned': u.is_banned,
                'created_at': u.created_at.isoformat()
            }
            for u in users
        ]
    }, 200


@admin_bp.route('/disputes', methods=['GET'])
@jwt_required()
def list_disputes():
    _, err = _require_admin()
    if err:
        return err

    disputes = Dispute.query.filter_by(status='open').order_by(Dispute.created_at.desc()).all()
    return {
        'disputes': [
            {
                'id': str(d.id),
                'transaction_id': str(d.transaction_id),
                'initiator_id': str(d.initiator_id),
                'reason': d.reason,
                'description': d.description,
                'status': d.status,
                'created_at': d.created_at.isoformat()
            }
            for d in disputes
        ]
    }, 200


@admin_bp.route('/disputes/<dispute_id>/resolve', methods=['PATCH'])
@jwt_required()
def resolve_dispute(dispute_id):
    _, err = _require_admin()
    if err:
        return err

    try:
        dispute = Dispute.query.filter_by(id=UUID(dispute_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid dispute ID'}}, 400

    if not dispute:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Dispute not found'}}, 404

    data = request.get_json() or {}
    dispute.status = 'resolved'

    txn = Transaction.query.get(dispute.transaction_id)
    if txn:
        resolution = data.get('resolution', 'favour_buyer')
        txn.status = 'completed' if resolution == 'favour_buyer' else 'cancelled'

    db.session.commit()
    return {'message': 'Dispute resolved', 'status': 'resolved'}, 200
