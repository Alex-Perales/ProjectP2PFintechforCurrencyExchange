"""Exchange rates — /api/v1/exchange/*"""
from flask import Blueprint, request
from flask_jwt_extended import jwt_required
from app.models import ExchangeRate

exchange_bp = Blueprint('exchange', __name__, url_prefix='/exchange')


@exchange_bp.route('/rates', methods=['GET'])
@jwt_required()
def get_rates():
    from_c = request.args.get('from_currency')
    to_c = request.args.get('to_currency')

    query = ExchangeRate.query
    if from_c:
        query = query.filter_by(from_currency=from_c.upper())
    if to_c:
        query = query.filter_by(to_currency=to_c.upper())

    rates = query.all()
    return {
        'rates': [
            {
                'id': r.id,
                'from_currency': r.from_currency,
                'to_currency': r.to_currency,
                'rate': r.rate,
                'updated_at': r.updated_at.isoformat() if r.updated_at else None,
            }
            for r in rates
        ]
    }, 200
