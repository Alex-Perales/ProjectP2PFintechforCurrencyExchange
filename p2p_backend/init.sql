-- P2P Exchange Database Schema — PostgreSQL 15
-- Plataforma de intercambio de divisas FIAT (PEN, USD, EUR, BRL)

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(120) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    dni VARCHAR(20) UNIQUE,
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    role VARCHAR(20) DEFAULT 'buyer',
    kyc_verified BOOLEAN DEFAULT FALSE,
    rating FLOAT DEFAULT 0.0,
    total_transactions INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    is_banned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS currencies (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exchange_rates (
    id VARCHAR(36) PRIMARY KEY,
    from_currency VARCHAR(10) NOT NULL,
    to_currency VARCHAR(10) NOT NULL,
    rate FLOAT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bank_accounts (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_holder VARCHAR(255) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_bank_accounts_user_id ON bank_accounts(user_id);

CREATE TABLE IF NOT EXISTS offers (
    id VARCHAR(36) PRIMARY KEY,
    vendor_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    from_currency VARCHAR(10) NOT NULL,
    to_currency VARCHAR(10) NOT NULL,
    amount FLOAT NOT NULL,
    available_amount FLOAT NOT NULL,
    price_per_unit FLOAT NOT NULL,
    offer_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    min_transaction FLOAT DEFAULT 0,
    max_transaction FLOAT,
    payment_methods TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_offers_vendor_id ON offers(vendor_id);
CREATE INDEX IF NOT EXISTS idx_offers_status ON offers(status);

CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY,
    offer_id VARCHAR(36) NOT NULL REFERENCES offers(id),
    buyer_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vendor_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount_from FLOAT NOT NULL,
    amount_to FLOAT NOT NULL,
    exchange_rate FLOAT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    buyer_payment_account TEXT,
    vendor_payment_account TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_transactions_buyer_id ON transactions(buyer_id);
CREATE INDEX IF NOT EXISTS idx_transactions_vendor_id ON transactions(vendor_id);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);

CREATE TABLE IF NOT EXISTS vouchers (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL REFERENCES transactions(id),
    sender_id VARCHAR(36) NOT NULL REFERENCES users(id),
    image_url VARCHAR(500) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ratings (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL REFERENCES transactions(id),
    rater_id VARCHAR(36) NOT NULL REFERENCES users(id),
    ratee_id VARCHAR(36) NOT NULL REFERENCES users(id),
    score INTEGER NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ratings_rater_id ON ratings(rater_id);

CREATE TABLE IF NOT EXISTS disputes (
    id VARCHAR(36) PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL REFERENCES transactions(id),
    initiator_id VARCHAR(36) NOT NULL REFERENCES users(id),
    reason VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'open',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_disputes_transaction_id ON disputes(transaction_id);

CREATE TABLE IF NOT EXISTS audit_logs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(100) NOT NULL,
    changes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Los usuarios y monedas de prueba los inserta seed.py al arrancar el contenedor
