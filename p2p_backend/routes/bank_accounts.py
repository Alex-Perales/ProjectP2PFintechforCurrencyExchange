from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import db
from models import BankAccount
from uuid import UUID

bank_accounts_bp = Blueprint('bank_accounts', __name__, url_prefix='/api/v1')


def _account_dict(a):
    return {
        'id': str(a.id),
        'user_id': str(a.user_id),
        'bank_name': a.bank_name,
        'account_number': a.account_number,
        'account_holder': a.account_holder,
        'account_type': a.account_type,
        'currency': a.currency,
        'is_primary': a.is_primary,
        'is_verified': a.is_verified,
        'created_at': a.created_at.isoformat()
    }


@bank_accounts_bp.route('/bank-accounts', methods=['GET'])
@jwt_required()
def list_accounts():
    user_id = get_jwt_identity()
    accounts = BankAccount.query.filter_by(user_id=UUID(user_id)).all()
    return {'bank_accounts': [_account_dict(a) for a in accounts]}, 200


@bank_accounts_bp.route('/bank-accounts', methods=['POST'])
@jwt_required()
def create_account():
    user_id = get_jwt_identity()
    data = request.get_json()
    if not data:
        return {'error': {'code': 'MISSING_DATA', 'message': 'Request body required'}}, 400

    account = BankAccount(
        user_id=UUID(user_id),
        bank_name=data.get('bank_name', ''),
        account_number=data.get('account_number', ''),
        account_holder=data.get('account_holder', ''),
        account_type=data.get('account_type', 'savings'),
        currency=data.get('currency', 'PEN'),
        is_primary=data.get('is_primary', False)
    )

    # If marked as primary, unmark others
    if account.is_primary:
        BankAccount.query.filter_by(
            user_id=UUID(user_id), is_primary=True
        ).update({'is_primary': False})

    db.session.add(account)
    db.session.commit()
    return _account_dict(account), 201


@bank_accounts_bp.route('/bank-accounts/<account_id>', methods=['DELETE'])
@jwt_required()
def delete_account(account_id):
    user_id = get_jwt_identity()
    try:
        account = BankAccount.query.filter_by(id=UUID(account_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid account ID'}}, 400

    if not account:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Account not found'}}, 404
    if str(account.user_id) != user_id:
        return {'error': {'code': 'FORBIDDEN', 'message': 'Not your account'}}, 403

    db.session.delete(account)
    db.session.commit()
    return {'message': 'Account deleted'}, 200
