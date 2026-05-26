"""Ratings — /api/v1/ratings/*"""
from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from sqlalchemy import func
from app.core.database import db
from app.core.exceptions import NotFoundError, AuthorizationError, AppException, ConflictError
from app.models import Transaction, Rating
from app.models.user import User

ratings_bp = Blueprint('ratings', __name__, url_prefix='/ratings')


@ratings_bp.route('', methods=['POST'])
@ratings_bp.route('/', methods=['POST'])
@jwt_required()
def create_rating():
    user_id = get_jwt_identity()
    data = request.get_json() or {}

    txn = db.session.get(Transaction, data.get('transaction_id'))
    if not txn:
        raise NotFoundError('Transaction not found')
    if txn.status != 'completed':
        raise AppException('INVALID_STATE', 'Can only rate completed transactions', 400)
    if txn.buyer_id != user_id and txn.vendor_id != user_id:
        raise AuthorizationError('Not your transaction')

    existing = Rating.query.filter_by(transaction_id=txn.id, rater_id=user_id).first()
    if existing:
        raise ConflictError('Already rated this transaction')

    score = data.get('score', 5)
    if not isinstance(score, int) or not (1 <= score <= 5):
        raise AppException('INVALID_SCORE', 'Score must be 1-5', 400)

    ratee_id = txn.vendor_id if txn.buyer_id == user_id else txn.buyer_id

    rating = Rating(
        transaction_id=txn.id,
        rater_id=user_id,
        ratee_id=ratee_id,
        score=score,
        comment=data.get('comment'),
    )
    db.session.add(rating)

    ratee = db.session.get(User, ratee_id)
    if ratee:
        avg = db.session.query(func.avg(Rating.score)).filter_by(ratee_id=ratee_id).scalar() or score
        ratee.rating = round(float(avg), 2)

    db.session.commit()
    return {'id': rating.id, 'score': rating.score, 'message': 'Rating submitted'}, 201
