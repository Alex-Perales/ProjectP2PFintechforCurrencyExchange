-- Tabla: bank_accounts  (depende de: users)
CREATE TABLE IF NOT EXISTS bank_accounts (
    id             VARCHAR(36)  PRIMARY KEY,
    user_id        VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bank_name      VARCHAR(100) NOT NULL,
    account_number VARCHAR(50)  NOT NULL,
    account_holder VARCHAR(255) NOT NULL,
    account_type   VARCHAR(20)  NOT NULL,
    currency       VARCHAR(10)  NOT NULL,
    is_primary     BOOLEAN      DEFAULT FALSE,
    is_verified    BOOLEAN      DEFAULT FALSE,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_bank_accounts_user_id ON bank_accounts(user_id);
