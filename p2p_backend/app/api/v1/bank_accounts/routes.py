"""Bank accounts — /api/v1/bank-accounts/*"""
from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.core.database import db
from app.core.exceptions import NotFoundError, AuthorizationError
from app.models import BankAccount

bank_accounts_bp = Blueprint('bank_accounts', __name__, url_prefix='/bank-accounts')


def _account_dict(a):
    return {
        'id': a.id,
        'user_id': a.user_id,
        'bank_name': a.bank_name,
        'account_number': a.account_number,
        'account_holder': a.account_holder,
        'account_type': a.account_type,
        'currency': a.currency,
        'is_primary': a.is_primary,
        'is_verified': a.is_verified,
        'created_at': a.created_at.isoformat(),
    }


@bank_accounts_bp.route('', methods=['GET'])
@bank_accounts_bp.route('/', methods=['GET'])
@jwt_required()
def list_accounts():
    user_id = get_jwt_identity()
    accounts = BankAccount.query.filter_by(user_id=user_id).all()
    return {'bank_accounts': [_account_dict(a) for a in accounts]}, 200


@bank_accounts_bp.route('', methods=['POST'])
@bank_accounts_bp.route('/', methods=['POST'])
@jwt_required()
def create_account():
    user_id = get_jwt_identity()
    data = request.get_json() or {}

    if data.get('is_primary'):
        BankAccount.query.filter_by(user_id=user_id, is_primary=True).update({'is_primary': False})

    account = BankAccount(
        user_id=user_id,
        bank_name=data.get('bank_name', ''),
        account_number=data.get('account_number', ''),
        account_holder=data.get('account_holder', ''),
        account_type=data.get('account_type', 'savings'),
        currency=data.get('currency', 'PEN'),
        is_primary=data.get('is_primary', False),
    )
    db.session.add(account)
    db.session.commit()
    return _account_dict(account), 201


@bank_accounts_bp.route('/<account_id>', methods=['DELETE'])
@jwt_required()
def delete_account(account_id):
    user_id = get_jwt_identity()
    account = db.session.get(BankAccount, account_id)
    if not account:
        raise NotFoundError('Account not found')
    if account.user_id != user_id:
        raise AuthorizationError('Not your account')

    db.session.delete(account)
    db.session.commit()
    return {'message': 'Account deleted'}, 200
