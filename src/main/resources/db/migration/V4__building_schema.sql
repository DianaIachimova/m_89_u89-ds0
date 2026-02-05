CREATE TABLE IF NOT EXISTS buildings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL,
    street VARCHAR(200) NOT NULL,
    street_number VARCHAR(20) NOT NULL,
    city_id UUID NOT NULL,
    construction_year INTEGER NOT NULL,
    building_type VARCHAR(20) NOT NULL,
    number_of_floors INTEGER,
    surface_area NUMERIC(10, 2) NOT NULL,
    insured_value NUMERIC(15, 2) NOT NULL,
    flood_zone BOOLEAN,
    earthquake_risk_zone BOOLEAN,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_buildings_owner
        FOREIGN KEY (owner_id) REFERENCES clients(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_buildings_city
        FOREIGN KEY (city_id) REFERENCES cities(id)
        ON DELETE RESTRICT,
    CONSTRAINT ck_building_type CHECK (building_type IN ('RESIDENTIAL', 'OFFICE', 'INDUSTRIAL')),
    CONSTRAINT ck_building_street_not_empty CHECK (LENGTH(TRIM(street)) > 0),
    CONSTRAINT ck_building_street_number_not_empty CHECK (LENGTH(TRIM(street_number)) > 0),
    CONSTRAINT ck_building_construction_year CHECK (construction_year >= 1800 AND construction_year <= 2100),
    CONSTRAINT ck_building_number_of_floors CHECK (number_of_floors IS NULL OR (number_of_floors >= 1 AND number_of_floors <= 200)),
    CONSTRAINT ck_building_surface_area CHECK (surface_area > 0),
    CONSTRAINT ck_building_insured_value CHECK (insured_value > 0)
);

CREATE INDEX IF NOT EXISTS idx_buildings_owner ON buildings(owner_id);

CREATE INDEX IF NOT EXISTS idx_buildings_city ON buildings(city_id);

CREATE INDEX IF NOT EXISTS idx_buildings_type ON buildings(building_type);

CREATE INDEX IF NOT EXISTS idx_buildings_risk_zones ON buildings(flood_zone, earthquake_risk_zone);

