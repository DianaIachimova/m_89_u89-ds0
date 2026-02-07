CREATE TABLE IF NOT EXISTS currencies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(3) NOT NULL,
    name VARCHAR(100) NOT NULL,
    exchange_rate_to_base DECIMAL(16, 6) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_currencies_code UNIQUE (code),
    CONSTRAINT ck_currencies_code_length CHECK (LENGTH(TRIM(code)) = 3),
    CONSTRAINT ck_currencies_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT ck_currencies_exchange_rate_positive CHECK (exchange_rate_to_base > 0)
    );

CREATE INDEX IF NOT EXISTS idx_currencies_code ON currencies(code);
CREATE INDEX IF NOT EXISTS idx_currencies_is_active ON currencies(is_active);
