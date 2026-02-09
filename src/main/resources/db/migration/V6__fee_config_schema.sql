CREATE TABLE IF NOT EXISTS fee_configurations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(30) NOT NULL,
    percentage DECIMAL(5, 4) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    is_active BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_fee_configurations_code_not_empty CHECK (LENGTH(TRIM(code)) > 3),
    CONSTRAINT ck_fee_configurations_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT ck_fee_configurations_type_valid CHECK (type IN ('BROKER_COMMISSION', 'RISK_ADJUSTMENT', 'ADMIN_FEE')),
    CONSTRAINT ck_fee_configurations_percentage_range CHECK (percentage >= 0 AND percentage <= 0.5),
    CONSTRAINT ck_fee_configurations_effective_period CHECK (effective_to IS NULL OR effective_from <= effective_to),
    CONSTRAINT uk_fee_configurations_code_type_active_period
    UNIQUE (code, type, is_active, effective_from, effective_to)
    );

CREATE INDEX IF NOT EXISTS idx_fee_configurations_code ON fee_configurations(code);
CREATE INDEX IF NOT EXISTS idx_fee_configurations_is_active ON fee_configurations(is_active);
CREATE INDEX IF NOT EXISTS idx_fee_configurations_effective_from_to ON fee_configurations(effective_from, effective_to);