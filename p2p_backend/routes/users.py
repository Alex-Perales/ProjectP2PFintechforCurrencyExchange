from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import db
from models import User
from uuid import UUID

users_bp = Blueprint('users', __name__, url_prefix='/api/v1')


@users_bp.route('/users/me', methods=['GET'])
@jwt_required()
def get_profile():
    user_id = get_jwt_identity()
    user = User.query.filter_by(id=UUID(user_id)).first()
    if not user:
        return {'error': {'code': 'NOT_FOUND', 'message': 'User not found'}}, 404
    return user.to_dict(), 200


@users_bp.route('/users/profile', methods=['PATCH'])
@jwt_required()
def update_profile():
    user_id = get_jwt_identity()
    user = User.query.filter_by(id=UUID(user_id)).first()
    if not user:
        return {'error': {'code': 'NOT_FOUND', 'message': 'User not found'}}, 404

    data = request.get_json() or {}
    for field in ('full_name', 'phone', 'avatar_url'):
        if field in data:
            setattr(user, field, data[field])

    db.session.commit()
    return user.to_dict(), 200


@users_bp.route('/users/<user_id>/profile', methods=['GET'])
def get_public_profile(user_id):
    try:
        user = User.query.filter_by(id=UUID(user_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid user ID'}}, 400

    if not user:
        return {'error': {'code': 'NOT_FOUND', 'message': 'User not found'}}, 404

    return {
        'id': str(user.id),
        'full_name': user.full_name,
        'avatar_url': user.avatar_url,
        'rating': user.rating,
        'total_transactions': user.total_transactions,
        'role': user.role,
        'kyc_verified': user.kyc_verified
    }, 200
