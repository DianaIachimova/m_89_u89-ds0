CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS countries (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(80) NOT NULL,
    CONSTRAINT uq_country_name UNIQUE (name),
    CONSTRAINT ck_country_name_not_empty CHECK (LENGTH(TRIM(name)) > 0)
    );

CREATE TABLE IF NOT EXISTS counties (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    code VARCHAR(10) NOT NULL,
    country_id UUID NOT NULL,
    CONSTRAINT fk_counties_country
    FOREIGN KEY (country_id) REFERENCES countries(id)
    ON DELETE RESTRICT,
    CONSTRAINT uq_counties_country UNIQUE (country_id, code),
    CONSTRAINT ck_county_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT ck_county_code_not_empty CHECK (LENGTH(TRIM(code)) > 0)
    );

CREATE INDEX IF NOT EXISTS idx_counties_country_id ON counties(country_id);

CREATE TABLE IF NOT EXISTS cities (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name  VARCHAR(180) NOT NULL,
    county_id UUID NOT NULL,
    CONSTRAINT fk_cities_county
    FOREIGN KEY (county_id) REFERENCES counties(id)
    ON DELETE RESTRICT,
    CONSTRAINT uk_cities_county UNIQUE (county_id, name),
    CONSTRAINT ck_city_name_not_empty CHECK (LENGTH(TRIM(name)) > 0)
    );

CREATE INDEX IF NOT EXISTS idx_cities_county_id ON cities(county_id);
