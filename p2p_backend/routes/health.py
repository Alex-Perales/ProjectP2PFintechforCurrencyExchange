from flask import Blueprint

health_bp = Blueprint('health', __name__)

@health_bp.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint for Docker"""
    return {'status': 'healthy'}, 200
