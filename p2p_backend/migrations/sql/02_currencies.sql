-- Tabla: currencies
CREATE TABLE IF NOT EXISTS currencies (
    id         VARCHAR(36)  PRIMARY KEY,
    code       VARCHAR(10)  UNIQUE NOT NULL,
    name       VARCHAR(100) NOT NULL,
    symbol     VARCHAR(10)  NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
