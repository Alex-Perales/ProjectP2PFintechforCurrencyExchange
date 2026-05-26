"""Error handlers globales"""

class AppException(Exception):
    """Excepción base de la aplicación"""
    def __init__(self, code: str, message: str, status_code: int = 400):
        self.code = code
        self.message = message
        self.status_code = status_code
        super().__init__(self.message)

class ValidationError(AppException):
    def __init__(self, message: str):
        super().__init__('VALIDATION_ERROR', message, 400)

class AuthenticationError(AppException):
    def __init__(self, message: str = 'Unauthorized'):
        super().__init__('UNAUTHORIZED', message, 401)

class AuthorizationError(AppException):
    def __init__(self, message: str = 'Forbidden'):
        super().__init__('FORBIDDEN', message, 403)

class NotFoundError(AppException):
    def __init__(self, message: str = 'Resource not found'):
        super().__init__('NOT_FOUND', message, 404)

class ConflictError(AppException):
    def __init__(self, message: str = 'Conflict'):
        super().__init__('CONFLICT', message, 409)

def register_error_handlers(app):
    """Registrar manejadores de errores en la app"""
    
    @app.errorhandler(AppException)
    def handle_app_exception(error):
        return {
            'error': {
                'code': error.code,
                'message': error.message
            }
        }, error.status_code
    
    @app.errorhandler(400)
    def handle_bad_request(error):
        return {'error': {'code': 'BAD_REQUEST', 'message': 'Bad request'}}, 400
    
    @app.errorhandler(404)
    def handle_not_found(error):
        return {'error': {'code': 'NOT_FOUND', 'message': 'Endpoint not found'}}, 404
    
    @app.errorhandler(500)
    def handle_internal_error(error):
        return {'error': {'code': 'INTERNAL_ERROR', 'message': 'Internal server error'}}, 500
