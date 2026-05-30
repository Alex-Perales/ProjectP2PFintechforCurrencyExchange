"""Offers routes — /api/v1/offers/*"""
from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.core.database import db
from app.core.exceptions import NotFoundError, AuthorizationError
from app.models import Offer
from app.models.user import User
import json

offers_bp = Blueprint('offers', __name__, url_prefix='/offers')


def _offer_dict(o, with_vendor=False):
    d = {
        'id': o.id,
        'vendor_id': o.vendor_id,
        'currency': o.from_currency,
        'fiat_currency': o.to_currency,
        'amount': o.amount,
        'available_amount': o.available_amount,
        'price_per_unit': o.price_per_unit,
        'offer_type': o.offer_type,
        'status': o.status,
        'min_transaction': o.min_transaction,
        'max_transaction': o.max_transaction,
        'payment_methods': json.loads(o.payment_methods) if o.payment_methods else [],
        'created_at': o.created_at.isoformat(),
    }
    if with_vendor:
        vendor = db.session.get(User, o.vendor_id)
        d['vendor'] = vendor.to_dict() if vendor else None
    return d


@offers_bp.route('', methods=['GET'])
@offers_bp.route('/', methods=['GET'])
@jwt_required(optional=True)
def list_offers():
    current_user_id = get_jwt_identity()
    currency = request.args.get('currency')
    fiat = request.args.get('fiat_currency')
    offer_type = request.args.get('type')

    query = Offer.query.filter_by(status='active')
    if currency:
        query = query.filter_by(from_currency=currency)
    if fiat:
        query = query.filter_by(to_currency=fiat)
    if offer_type:
        query = query.filter_by(offer_type=offer_type)
    if current_user_id:
        query = query.filter(Offer.vendor_id != current_user_id)

    offers = query.order_by(Offer.price_per_unit).all()
    return {'offers': [_offer_dict(o, with_vendor=True) for o in offers]}, 200


@offers_bp.route('/<offer_id>', methods=['GET'])
def get_offer(offer_id):
    offer = db.session.get(Offer, offer_id)
    if not offer:
        raise NotFoundError('Offer not found')
    return _offer_dict(offer, with_vendor=True), 200


@offers_bp.route('', methods=['POST'])
@offers_bp.route('/', methods=['POST'])
@jwt_required()
def create_offer():
    user_id = get_jwt_identity()
    user = db.session.get(User, user_id)
    if not user or user.role not in ('vendor', 'admin'):
        raise AuthorizationError('Only vendors can create offers')

    data = request.get_json() or {}
    offer = Offer(
        vendor_id=user_id,
        from_currency=data.get('currency', 'USD'),
        to_currency=data.get('fiat_currency', 'PEN'),
        amount=data.get('amount', 0),
        available_amount=data.get('amount', 0),
        price_per_unit=data.get('price_per_unit', 0),
        offer_type=data.get('offer_type', 'sell'),
        min_transaction=data.get('min_transaction', 0),
        max_transaction=data.get('max_transaction'),
        payment_methods=json.dumps(data.get('payment_methods', []))
    )
    db.session.add(offer)
    db.session.commit()
    return _offer_dict(offer), 201


@offers_bp.route('/my-offers', methods=['GET'])
@jwt_required()
def my_offers():
    user_id = get_jwt_identity()
    offers = Offer.query.filter_by(vendor_id=user_id).order_by(Offer.created_at.desc()).all()
    return {'offers': [_offer_dict(o) for o in offers]}, 200


@offers_bp.route('/match', methods=['POST'])
@jwt_required()
def match_offer():
    user_id = get_jwt_identity()
    data = request.get_json() or {}
    currency = data.get('currency', 'USD')
    fiat_currency = data.get('fiat_currency', 'PEN')
    offer_type = data.get('offer_type')  # optional — don't filter if not provided
    amount = data.get('amount', 0)

    query = Offer.query.filter_by(
        status='active', from_currency=currency,
        to_currency=fiat_currency
    ).filter(Offer.vendor_id != user_id)

    if offer_type:
        query = query.filter_by(offer_type=offer_type)
    if amount:
        query = query.filter(
            Offer.min_transaction <= amount,
            (Offer.max_transaction == None) | (Offer.max_transaction >= amount)  # noqa
        )

    offer = query.order_by(Offer.price_per_unit).first()
    if not offer:
        raise NotFoundError('No matching offer found')

    return _offer_dict(offer, with_vendor=True), 200


@offers_bp.route('/<offer_id>', methods=['PATCH'])
@jwt_required()
def update_offer(offer_id):
    user_id = get_jwt_identity()
    offer = db.session.get(Offer, offer_id)
    if not offer:
        raise NotFoundError('Offer not found')
    if offer.vendor_id != user_id:
        raise AuthorizationError('Not your offer')

    data = request.get_json() or {}
    for field in ('price_per_unit', 'status', 'available_amount', 'min_transaction', 'max_transaction'):
        if field in data:
            setattr(offer, field, data[field])

    db.session.commit()
    return _offer_dict(offer), 200


@offers_bp.route('/<offer_id>', methods=['DELETE'])
@jwt_required()
def delete_offer(offer_id):
    user_id = get_jwt_identity()
    offer = db.session.get(Offer, offer_id)
    if not offer:
        raise NotFoundError('Offer not found')
    if offer.vendor_id != user_id:
        raise AuthorizationError('Not your offer')

    offer.status = 'closed'
    db.session.commit()
    return {'message': 'Offer cancelled'}, 200
