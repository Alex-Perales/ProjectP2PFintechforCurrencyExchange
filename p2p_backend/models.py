from app import db
from sqlalchemy.dialects.postgresql import UUID
from datetime import datetime
import uuid
from werkzeug.security import generate_password_hash, check_password_hash

class User(db.Model):
    __tablename__ = 'users'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    email = db.Column(db.String(120), unique=True, nullable=False, index=True)
    password_hash = db.Column(db.String(255), nullable=False)
    full_name = db.Column(db.String(255), nullable=False)
    dni = db.Column(db.String(20), unique=True)
    phone = db.Column(db.String(20))
    avatar_url = db.Column(db.String(500))
    role = db.Column(db.String(20), default='buyer')  # buyer, vendor, admin
    kyc_verified = db.Column(db.Boolean, default=False)
    rating = db.Column(db.Float, default=0.0)
    total_transactions = db.Column(db.Integer, default=0)
    is_active = db.Column(db.Boolean, default=True)
    is_banned = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    def set_password(self, password):
        self.password_hash = generate_password_hash(password)
    
    def check_password(self, password):
        return check_password_hash(self.password_hash, password)
    
    def to_dict(self):
        return {
            'id': str(self.id),
            'email': self.email,
            'full_name': self.full_name,
            'role': self.role,
            'kyc_verified': self.kyc_verified,
            'rating': self.rating,
            'total_transactions': self.total_transactions,
            'is_active': self.is_active
        }

class Currency(db.Model):
    __tablename__ = 'currencies'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    code = db.Column(db.String(10), unique=True, nullable=False)
    name = db.Column(db.String(100), nullable=False)
    symbol = db.Column(db.String(10), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

class ExchangeRate(db.Model):
    __tablename__ = 'exchange_rates'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    from_currency = db.Column(db.String(10), nullable=False)
    to_currency = db.Column(db.String(10), nullable=False)
    rate = db.Column(db.Float, nullable=False)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

class BankAccount(db.Model):
    __tablename__ = 'bank_accounts'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = db.Column(UUID(as_uuid=True), db.ForeignKey('users.id', ondelete='CASCADE'), nullable=False, index=True)
    bank_name = db.Column(db.String(100), nullable=False)
    account_number = db.Column(db.String(50), nullable=False)
    account_holder = db.Column(db.String(255), nullable=False)
    account_type = db.Column(db.String(20), nullable=False)  # savings, checking
    currency = db.Column(db.String(10), nullable=False)
    is_primary = db.Column(db.Boolean, default=False)
    is_verified = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

class Offer(db.Model):
    __tablename__ = 'offers'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    vendor_id = db.Column(UUID(as_uuid=True), db.ForeignKey('users.id', ondelete='CASCADE'), nullable=False, index=True)
    currency = db.Column(db.String(10), nullable=False)  # crypto (BTC, ETH)
    fiat_currency = db.Column(db.String(10), nullable=False)  # PEN, USD
    amount = db.Column(db.Float, nullable=False)
    available_amount = db.Column(db.Float, nullable=False)
    price_per_unit = db.Column(db.Float, nullable=False)
    offer_type = db.Column(db.String(20), nullable=False)  # buy, sell
    status = db.Column(db.String(20), default='active', index=True)  # active, paused, closed
    min_transaction = db.Column(db.Float, default=0)
    max_transaction = db.Column(db.Float)
    payment_methods = db.Column(db.String(500))  # JSON string
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

class Transaction(db.Model):
    __tablename__ = 'transactions'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    offer_id = db.Column(UUID(as_uuid=True), db.ForeignKey('offers.id'), nullable=False)
    buyer_id = db.Column(UUID(as_uuid=True), db.ForeignKey('users.id', ondelete='CASCADE'), nullable=False, index=True)
    vendor_id = db.Column(UUID(as_uuid=True), db.ForeignKey('users.id', ondelete='CASCADE'), nullable=False, index=True)
    amount_crypto = db.Column(db.Float, nullable=False)
    amount_fiat = db.Column(db.Float, nullable=False)
    exchange_rate = db.Column(db.Float, nullable=False)
    status = db.Column(db.String(20), default='pending', index=True)  # pending, completed, cancelled, disputed
    buyer_payment_account = db.Column(db.String(500))  # JSON string
    vendor_payment_account = db.Column(db.String(500))  # JSON string
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

class Voucher(db.Model):
    __tablename__ = 'vouchers'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    transaction_id = db.Column(UUID(as_uuid=True), db.ForeignKey('transactions.id'), nullable=False)
    sender_id = db.Column(UUID(as_uuid=True), db.ForeignKey('users.id'), nullable=False)
    image_url = db.Column(db.String(500), nullable=False)
    description = db.Column(db.Text)
    status = db.Column(db.String(20), default='pending')  # pending, verified, rejected
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

class Rating(db.Model):
    __tablename__ = 'ratings'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    transaction_id = db.Column(UUID(as_uuid=True), db.ForeignKey('transactions.id'), nullable=False)
    rater_id = db.Column(UUID(as_uuid=True), db.ForeignKey('users.id'), nullable=False, index=True)
    ratee_id = db.Column(UUID(as_uuid=True), db.ForeignKey('users.id'), nullable=False)
    score = db.Column(db.Integer, nullable=False)  # 1-5
    comment = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

class Dispute(db.Model):
    __tablename__ = 'disputes'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    transaction_id = db.Column(UUID(as_uuid=True), db.ForeignKey('transactions.id'), nullable=False, index=True)
    initiator_id = db.Column(UUID(as_uuid=True), db.ForeignKey('users.id'), nullable=False)
    reason = db.Column(db.String(255), nullable=False)
    description = db.Column(db.Text)
    status = db.Column(db.String(20), default='open')  # open, resolved, rejected
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

class AuditLog(db.Model):
    __tablename__ = 'audit_logs'
    
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = db.Column(UUID(as_uuid=True), db.ForeignKey('users.id'), nullable=True)
    action = db.Column(db.String(100), nullable=False)
    resource = db.Column(db.String(100), nullable=False)
    changes = db.Column(db.Text)  # JSON string
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
