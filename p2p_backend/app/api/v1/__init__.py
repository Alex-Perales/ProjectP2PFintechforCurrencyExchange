"""API v1 Blueprint"""
from flask import Blueprint

api_v1 = Blueprint('api_v1', __name__)

from app.api.v1.auth import routes as auth_routes
from app.api.v1.offers import routes as offers_routes
from app.api.v1.transactions import routes as transactions_routes
from app.api.v1.users import routes as users_routes
from app.api.v1.admin import routes as admin_routes
from app.api.v1.exchange import routes as exchange_routes
from app.api.v1.bank_accounts import routes as bank_accounts_routes
from app.api.v1.ratings import routes as ratings_routes

api_v1.register_blueprint(auth_routes.auth_bp)
api_v1.register_blueprint(offers_routes.offers_bp)
api_v1.register_blueprint(transactions_routes.transactions_bp)
api_v1.register_blueprint(users_routes.users_bp)
api_v1.register_blueprint(admin_routes.admin_bp)
api_v1.register_blueprint(exchange_routes.exchange_bp)
api_v1.register_blueprint(bank_accounts_routes.bank_accounts_bp)
api_v1.register_blueprint(ratings_routes.ratings_bp)
