"""Flask Application Factory"""
from flask import Flask
from flask_cors import CORS


def create_app(config_name='development'):
    from app.core.config import config
    from app.core.database import db
    from app.core.security import jwt

    app = Flask(__name__)
    app.config.from_object(config[config_name])

    db.init_app(app)
    jwt.init_app(app)
    CORS(app, resources={r"/api/*": {"origins": "*"}})

    with app.app_context():
        # Import ALL models so create_all() sees them
        from app.models.user import User  # noqa
        from app.models import (  # noqa
            Currency, ExchangeRate, BankAccount, Offer,
            Transaction, Voucher, Rating, Dispute, AuditLog
        )
        db.create_all()

        # Register blueprints
        from app.api.v1 import api_v1
        app.register_blueprint(api_v1, url_prefix='/api/v1')

        # Health check
        @app.route('/health')
        def health():
            return {'status': 'healthy'}, 200

        # Error handlers
        from app.core.exceptions import AppException

        @app.errorhandler(AppException)
        def handle_app_exception(error):
            return {'error': {'code': error.code, 'message': error.message}}, error.status_code

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
