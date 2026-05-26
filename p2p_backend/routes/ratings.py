from flask import Blueprint, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import db
from models import Rating, Transaction, User
from uuid import UUID
from sqlalchemy import func

ratings_bp = Blueprint('ratings', __name__, url_prefix='/api/v1')


@ratings_bp.route('/ratings', methods=['POST'])
@jwt_required()
def create_rating():
    user_id = get_jwt_identity()
    data = request.get_json()
    if not data:
        return {'error': {'code': 'MISSING_DATA', 'message': 'Request body required'}}, 400

    try:
        txn = Transaction.query.filter_by(id=UUID(data.get('transaction_id'))).first()
    except Exception:
        return {'error': {'code': 'INVALID_ID', 'message': 'Invalid transaction ID'}}, 400

    if not txn:
        return {'error': {'code': 'NOT_FOUND', 'message': 'Transaction not found'}}, 404
    if txn.status != 'completed':
        return {'error': {'code': 'INVALID_STATE', 'message': 'Can only rate completed transactions'}}, 400
    if str(txn.buyer_id) != user_id and str(txn.vendor_id) != user_id:
        return {'error': {'code': 'FORBIDDEN', 'message': 'Not your transaction'}}, 403

    # Determine who is being rated
    ratee_id = str(txn.vendor_id) if str(txn.buyer_id) == user_id else str(txn.buyer_id)

    # Prevent duplicate rating
    existing = Rating.query.filter_by(
        transaction_id=txn.id, rater_id=UUID(user_id)
    ).first()
    if existing:
        return {'error': {'code': 'ALREADY_RATED', 'message': 'Already rated this transaction'}}, 409

    score = data.get('score', 5)
    if not isinstance(score, int) or not (1 <= score <= 5):
        return {'error': {'code': 'INVALID_SCORE', 'message': 'Score must be 1-5'}}, 400

    rating = Rating(
        transaction_id=txn.id,
        rater_id=UUID(user_id),
        ratee_id=UUID(ratee_id),
        score=score,
        comment=data.get('comment')
    )
    db.session.add(rating)

    # Update ratee's average rating
    ratee = User.query.get(UUID(ratee_id))
    if ratee:
        avg = db.session.query(func.avg(Rating.score)).filter_by(
            ratee_id=UUID(ratee_id)
        ).scalar() or score
        ratee.rating = round(float(avg), 2)

    db.session.commit()
    return {
        'id': str(rating.id),
        'score': rating.score,
        'message': 'Rating submitted'
    }, 201
