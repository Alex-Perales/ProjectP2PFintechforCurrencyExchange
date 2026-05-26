from flask import Blueprint, request
from flask_jwt_extended import (
    create_access_token, create_refresh_token,
    jwt_required, get_jwt_identity
)
from app import db
from models import User
from uuid import UUID

auth_bp = Blueprint('auth', __name__, url_prefix='/api/v1/auth')


@auth_bp.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    if not data or not data.get('email') or not data.get('password'):
        return {'error': {'code': 'MISSING_FIELDS', 'message': 'Email and password required'}}, 400

    if User.query.filter_by(email=data['email']).first():
        return {'error': {'code': 'EMAIL_EXISTS', 'message': 'Email already registered'}}, 409

    user = User(
        email=data['email'],
        full_name=data.get('full_name', ''),
        dni=data.get('dni'),
        phone=data.get('phone'),
        role=data.get('role', 'buyer')
    )
    user.set_password(data['password'])

    db.session.add(user)
    db.session.commit()

    access_token = create_access_token(identity=str(user.id))
    refresh_token = create_refresh_token(identity=str(user.id))

    return {
        'id': str(user.id),
        'email': user.email,
        'full_name': user.full_name,
        'role': user.role,
        'access_token': access_token,
        'refresh_token': refresh_token,
        'message': 'User registered successfully'
    }, 201


@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    if not data or not data.get('email') or not data.get('password'):
        return {'error': {'code': 'MISSING_FIELDS', 'message': 'Email and password required'}}, 400

    user = User.query.filter_by(email=data['email']).first()

    if not user or not user.check_password(data['password']):
        return {'error': {'code': 'INVALID_CREDENTIALS', 'message': 'Invalid email or password'}}, 401

    if not user.is_active:
        return {'error': {'code': 'USER_INACTIVE', 'message': 'Account is inactive'}}, 403

    access_token = create_access_token(identity=str(user.id))
    refresh_token = create_refresh_token(identity=str(user.id))

    return {
        'id': str(user.id),
        'email': user.email,
        'full_name': user.full_name,
        'role': user.role,
        'kyc_verified': user.kyc_verified,
        'rating': user.rating,
        'avatar_url': user.avatar_url,
        'access_token': access_token,
        'refresh_token': refresh_token
    }, 200


@auth_bp.route('/refresh', methods=['POST'])
@jwt_required(refresh=True)
def refresh():
    user_id = get_jwt_identity()
    access_token = create_access_token(identity=user_id)
    return {'access_token': access_token}, 200


@auth_bp.route('/logout', methods=['POST'])
@jwt_required()
def logout():
    return {'message': 'Logged out successfully'}, 200


@auth_bp.route('/me', methods=['GET'])
@jwt_required()
def me():
    user_id = get_jwt_identity()
    user = User.query.filter_by(id=UUID(user_id)).first()
    if not user:
        return {'error': {'code': 'NOT_FOUND', 'message': 'User not found'}}, 404

    return {
        'id': str(user.id),
        'email': user.email,
        'full_name': user.full_name,
        'phone': user.phone,
        'role': user.role,
        'kyc_verified': user.kyc_verified,
        'rating': user.rating,
        'total_transactions': user.total_transactions,
        'avatar_url': user.avatar_url,
        'is_active': user.is_active
    }, 200
