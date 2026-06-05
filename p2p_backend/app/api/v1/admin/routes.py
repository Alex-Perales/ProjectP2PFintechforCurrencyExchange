"""Admin routes — /api/v1/admin/*"""
from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from sqlalchemy import func
from app.core.database import db
from app.core.exceptions import AuthorizationError, NotFoundError
from app.models import Transaction, Dispute
from app.models.user import User
from app.core.notifications import notify

admin_bp = Blueprint('admin', __name__, url_prefix='/admin')


def _require_admin():
    user_id = get_jwt_identity()
    user = db.session.get(User, user_id)
    if not user or user.role != 'admin':
        raise AuthorizationError('Admin only')
    return user


@admin_bp.route('/dashboard', methods=['GET'])
@jwt_required()
def dashboard():
    _require_admin()
    total_users = User.query.count()
    total_txns = Transaction.query.count()
    pending_disputes = Dispute.query.filter_by(status='open').count()
    total_volume = db.session.query(
        func.sum(Transaction.amount_to)
    ).filter_by(status='completed').scalar() or 0

    return {
        'total_users': total_users,
        'total_transactions': total_txns,
        'pending_disputes': pending_disputes,
        'total_volume': float(total_volume),
    }, 200


@admin_bp.route('/users', methods=['GET'])
@jwt_required()
def list_users():
    _require_admin()
    users = User.query.order_by(User.created_at.desc()).all()
    return {
        'users': [
            {
                'id': u.id,
                'email': u.email,
                'full_name': u.full_name,
                'role': u.role,
                'kyc_verified': u.kyc_verified,
                'rating': u.rating,
                'total_transactions': u.total_transactions,
                'is_active': u.is_active,
                'created_at': u.created_at.isoformat(),
            }
            for u in users
        ]
    }, 200


@admin_bp.route('/disputes', methods=['GET'])
@jwt_required()
def list_disputes():
    _require_admin()
    disputes = Dispute.query.filter_by(status='open').order_by(Dispute.created_at.desc()).all()
    return {
        'disputes': [
            {
                'id': d.id,
                'transaction_id': d.transaction_id,
                'initiator_id': d.initiator_id,
                'reason': d.reason,
                'description': d.description,
                'status': d.status,
                'created_at': d.created_at.isoformat(),
            }
            for d in disputes
        ]
    }, 200


@admin_bp.route('/disputes/<dispute_id>/resolve', methods=['PATCH'])
@jwt_required()
def resolve_dispute(dispute_id):
    _require_admin()
    dispute = db.session.get(Dispute, dispute_id)
    if not dispute:
        raise NotFoundError('Dispute not found')

    data = request.get_json() or {}
    dispute.status = 'resolved'
    resolution = data.get('resolution', '')

    txn = db.session.get(Transaction, dispute.transaction_id)
    if txn:
        txn.status = 'completed' if resolution == 'favour_buyer' else 'cancelled'
        resolution_msg = 'a favor del comprador' if resolution == 'favour_buyer' else 'a favor del vendedor'
        notify(
            user_id=txn.buyer_id,
            type='admin',
            title='Disputa resuelta',
            body=f'El administrador resolvió la disputa #{dispute.id[:8]} {resolution_msg}.',
            resource_id=dispute.id,
        )
        notify(
            user_id=txn.vendor_id,
            type='admin',
            title='Disputa resuelta',
            body=f'El administrador resolvió la disputa #{dispute.id[:8]} {resolution_msg}.',
            resource_id=dispute.id,
        )

    db.session.commit()
    return {'message': 'Dispute resolved', 'status': 'resolved'}, 200
