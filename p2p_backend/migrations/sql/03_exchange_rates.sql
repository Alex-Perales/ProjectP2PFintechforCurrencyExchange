-- Tabla: exchange_rates
CREATE TABLE IF NOT EXISTS exchange_rates (
    id            VARCHAR(36) PRIMARY KEY,
    from_currency VARCHAR(10) NOT NULL,
    to_currency   VARCHAR(10) NOT NULL,
    rate          FLOAT       NOT NULL,
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);
