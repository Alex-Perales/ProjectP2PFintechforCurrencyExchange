from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import db
from models import Offer, User, ExchangeRate
from uuid import UUID
import json

market_bp = Blueprint('market', __name__, url_prefix='/api/v1')


def _offer_dict(o, include_vendor=False):
    d = {
        'id': str(o.id),
        'vendor_id': str(o.vendor_id),
        'currency': o.currency,
        'fiat_currency': o.fiat_currency,
        'amount': o.amount,
        'available_amount': o.available_amount,
        'price_per_unit': o.price_per_unit,
        'offer_type': o.offer_type,
        'status': o.status,
        'min_transaction': o.min_transaction,
        'max_transaction': o.max_transaction,
        'payment_methods': json.loads(o.payment_methods) if o.payment_methods else [],
        'created_at': o.created_at.isoformat()
    }
    if include_vendor:
        vendor = User.query.get(o.vendor_id)
        d['vendor'] = vendor.to_dict() if vendor else None
    return d


@market_bp.route('/offers', methods=['GET'])
def list_offers():
    currency = request.args.get('currency')
    fiat = request.args.get('fiat_currency')
    offer_type = request.args.get('type')

    query = Offer.query.filter_by(status='active')
    if currency:
        query = query.filter_by(currency=currency)
    if fiat:
        query = query.filter_by(fiat_currency=fiat)
    if offer_type:
        query = query.filter_by(offer_type=offer_type)

    offers = query.order_by(Offer.price_per_unit).all()
    return {'offers': [_offer_dict(o, include_vendor=True) for o in offers]}, 200


@market_bp.route('/offers/<offer_id>', methods=['GET'])
def get_offer(offer_id):
    try:
        offer = Offer.query.filter_by(id=UUID(offer_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid offer ID'}}, 400

    if not offer:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Offer not found'}}, 404

    return _offer_dict(offer, include_vendor=True), 200


@market_bp.route('/offers', methods=['POST'])
@jwt_required()
def create_offer():
    user_id = get_jwt_identity()
    user = User.query.get(UUID(user_id))

    if not user or user.role not in ('vendor', 'admin'):
        return {'error': {'code': 'FORBIDDEN', 'message': 'Only vendors can create offers'}}, 403

    data = request.get_json()
    if not data:
        return {'error': {'code': 'MISSING_DATA', 'message': 'Request body required'}}, 400

    offer = Offer(
        vendor_id=UUID(user_id),
        currency=data.get('currency', 'USD'),
        fiat_currency=data.get('fiat_currency', 'PEN'),
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


@market_bp.route('/offers/my-offers', methods=['GET'])
@jwt_required()
def my_offers():
    user_id = get_jwt_identity()
    offers = Offer.query.filter_by(vendor_id=UUID(user_id)).order_by(Offer.created_at.desc()).all()
    return {'offers': [_offer_dict(o) for o in offers]}, 200


@market_bp.route('/offers/match', methods=['POST'])
@jwt_required()
def match_offer():
    """Return best offer for a currency pair"""
    data = request.get_json() or {}
    currency = data.get('currency', 'USD')
    fiat_currency = data.get('fiat_currency', 'PEN')
    offer_type = data.get('offer_type', 'sell')
    amount = data.get('amount', 0)

    query = Offer.query.filter_by(
        status='active',
        currency=currency,
        fiat_currency=fiat_currency,
        offer_type=offer_type
    )
    if amount:
        query = query.filter(
            Offer.min_transaction <= amount,
            (Offer.max_transaction == None) | (Offer.max_transaction >= amount)  # noqa
        )

    offer = query.order_by(Offer.price_per_unit).first()
    if not offer:
        return {'error': {'code': 'NO_MATCH', 'message': 'No matching offer found'}}, 404

    return _offer_dict(offer, include_vendor=True), 200


@market_bp.route('/offers/<offer_id>', methods=['PATCH'])
@jwt_required()
def update_offer(offer_id):
    user_id = get_jwt_identity()
    try:
        offer = Offer.query.filter_by(id=UUID(offer_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid offer ID'}}, 400

    if not offer:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Offer not found'}}, 404
    if str(offer.vendor_id) != user_id:
        return {'error': {'code': 'FORBIDDEN', 'message': 'Not your offer'}}, 403

    data = request.get_json() or {}
    for field in ('price_per_unit', 'status', 'available_amount', 'min_transaction', 'max_transaction'):
        if field in data:
            setattr(offer, field, data[field])

    db.session.commit()
    return _offer_dict(offer), 200


@market_bp.route('/offers/<offer_id>', methods=['DELETE'])
@jwt_required()
def delete_offer(offer_id):
    user_id = get_jwt_identity()
    try:
        offer = Offer.query.filter_by(id=UUID(offer_id)).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid offer ID'}}, 400

    if not offer:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Offer not found'}}, 404
    if str(offer.vendor_id) != user_id:
        return {'error': {'code': 'FORBIDDEN', 'message': 'Not your offer'}}, 403

    offer.status = 'closed'
    db.session.commit()
    return {'message': 'Offer cancelled'}, 200
