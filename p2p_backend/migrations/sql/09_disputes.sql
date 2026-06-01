-- Tabla: disputes  (depende de: transactions, users)
CREATE TABLE IF NOT EXISTS disputes (
    id             VARCHAR(36)  PRIMARY KEY,
    transaction_id VARCHAR(36)  NOT NULL REFERENCES transactions(id),
    initiator_id   VARCHAR(36)  NOT NULL REFERENCES users(id),
    reason         VARCHAR(255) NOT NULL,
    description    TEXT,
    status         VARCHAR(20)  DEFAULT 'open',
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_disputes_transaction_id ON disputes(transaction_id);
