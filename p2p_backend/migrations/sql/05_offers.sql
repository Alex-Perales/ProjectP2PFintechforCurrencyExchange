-- Tabla: offers  (depende de: users)
CREATE TABLE IF NOT EXISTS offers (
    id               VARCHAR(36) PRIMARY KEY,
    vendor_id        VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    from_currency    VARCHAR(10) NOT NULL,
    to_currency      VARCHAR(10) NOT NULL,
    amount           FLOAT       NOT NULL,
    available_amount FLOAT       NOT NULL,
    price_per_unit   FLOAT       NOT NULL,
    offer_type       VARCHAR(20) NOT NULL,
    status           VARCHAR(20) DEFAULT 'active',
    min_transaction  FLOAT       DEFAULT 0,
    max_transaction  FLOAT,
    payment_methods  TEXT,
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_offers_vendor_id ON offers(vendor_id);
CREATE INDEX IF NOT EXISTS idx_offers_status    ON offers(status);
