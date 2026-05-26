#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""P2P Exchange Backend — Flask Application Factory"""

from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import JWTManager
from flask_cors import CORS
import os
from dotenv import load_dotenv

load_dotenv()

db = SQLAlchemy()
jwt = JWTManager()


def create_app(config_name=None):
    from config import config

    if config_name is None:
        config_name = os.getenv('FLASK_ENV', 'development')

    app = Flask(__name__)
    app.config.from_object(config[config_name])

    db.init_app(app)
    jwt.init_app(app)
    CORS(app, resources={r"/api/*": {"origins": "*"}})

    with app.app_context():
        db.create_all()

    # Blueprints
    from routes.auth import auth_bp
    from routes.market import market_bp
    from routes.transactions import transactions_bp
    from routes.users import users_bp
    from routes.admin import admin_bp
    from routes.health import health_bp
    from routes.exchange import exchange_bp
    from routes.bank_accounts import bank_accounts_bp
    from routes.ratings import ratings_bp

    app.register_blueprint(auth_bp)
    app.register_blueprint(market_bp)
    app.register_blueprint(transactions_bp)
    app.register_blueprint(users_bp)
    app.register_blueprint(admin_bp)
    app.register_blueprint(health_bp)
    app.register_blueprint(exchange_bp)
    app.register_blueprint(bank_accounts_bp)
    app.register_blueprint(ratings_bp)

    # Error handlers
    @app.errorhandler(400)
    def bad_request(e):
        return {'error': {'code': 'BAD_REQUEST', 'message': str(e)}}, 400

    @app.errorhandler(401)
    def unauthorized(e):
        return {'error': {'code': 'UNAUTHORIZED', 'message': str(e)}}, 401

    @app.errorhandler(403)
    def forbidden(e):
        return {'error': {'code': 'FORBIDDEN', 'message': str(e)}}, 403

    @app.errorhandler(404)
    def not_found(e):
        return {'error': {'code': 'NOT_FOUND', 'message': str(e)}}, 404

    @app.errorhandler(500)
    def internal_error(e):
        db.session.rollback()
        return {'error': {'code': 'INTERNAL_ERROR', 'message': str(e)}}, 500

    return app


if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=5000, debug=True)
