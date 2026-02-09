CREATE TABLE IF NOT EXISTS risk_factor_configurations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    level VARCHAR(30) NOT NULL,
    reference_id UUID,
    building_type VARCHAR(30),
    adjustment_percentage DECIMAL(5, 4) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_rfc_level_valid CHECK (level IN ('COUNTRY', 'COUNTY', 'CITY', 'BUILDING_TYPE')),
    CONSTRAINT ck_rfc_building_type_valid
    CHECK (building_type IS NULL OR building_type IN ('RESIDENTIAL', 'OFFICE', 'INDUSTRIAL')),
    CONSTRAINT ck_rfc_percentage_range
    CHECK (adjustment_percentage >= -0.50 AND adjustment_percentage <= 1.00),
    CONSTRAINT ck_rfc_target_exclusive
    CHECK (
        (level = 'BUILDING_TYPE' AND building_type IS NOT NULL AND reference_id IS NULL)
     OR
        (level <> 'BUILDING_TYPE' AND reference_id IS NOT NULL AND building_type IS NULL)
    ));

    CREATE UNIQUE INDEX uk_rfc_active_geo
    ON risk_factor_configurations (level, reference_id)
    WHERE is_active = true AND reference_id IS NOT NULL;

    CREATE UNIQUE INDEX uk_rfc_active_building_type
    ON risk_factor_configurations (level, building_type)
    WHERE is_active = true AND building_type IS NOT NULL;