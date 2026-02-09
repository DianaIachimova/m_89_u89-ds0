CREATE TABLE IF NOT EXISTS brokers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    broker_code VARCHAR(30) NOT NULL,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(10) NOT NULL,
    commission_percentage DECIMAL(5, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_brokers_code UNIQUE (broker_code),
    CONSTRAINT uk_brokers_email UNIQUE (email),
    CONSTRAINT ck_brokers_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT ck_brokers_code_format CHECK (broker_code ~ '^[A-Z0-9_-]{3,30}$'),
    CONSTRAINT ck_brokers_name_not_empty CHECK (LENGTH(TRIM(name)) >= 2),
    CONSTRAINT ck_brokers_email_not_empty CHECK (LENGTH(TRIM(email)) > 0),
    CONSTRAINT ck_brokers_commission_range CHECK (
        commission_percentage IS NULL OR (commission_percentage >= 0 AND commission_percentage <= 100)
    )
);

CREATE UNIQUE INDEX idx_brokers_code_ci ON brokers (LOWER(broker_code));
CREATE UNIQUE INDEX idx_brokers_email_ci ON brokers (LOWER(email));
CREATE INDEX idx_brokers_status ON brokers (status);

