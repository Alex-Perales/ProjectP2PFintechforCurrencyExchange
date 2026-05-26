"""Auth service"""
from app.core.exceptions import AuthenticationError, ConflictError
from app.core.security import verify_password
from app.models.user import User
from app.core.database import db

class AuthService:
    @staticmethod
    def register(email: str, password: str, full_name: str, role: str = 'buyer') -> User:
        """Registrar nuevo usuario"""
        if User.query.filter_by(email=email).first():
            raise ConflictError('Email already registered')
        
        user = User(email=email, full_name=full_name, role=role)
        user.set_password(password)
        
        db.session.add(user)
        db.session.commit()
        return user
    
    @staticmethod
    def login(email: str, password: str) -> User:
        """Autenticar usuario"""
        user = User.query.filter_by(email=email).first()
        if not user or not user.check_password(password):
            raise AuthenticationError('Invalid credentials')
        if not user.is_active:
            raise AuthenticationError('User inactive')
        return user
    
    @staticmethod
    def get_user_by_id(user_id: str) -> User:
        """Obtener usuario por ID"""
        user = User.query.get(user_id)
        if not user:
            raise AuthenticationError('User not found')
        return user
