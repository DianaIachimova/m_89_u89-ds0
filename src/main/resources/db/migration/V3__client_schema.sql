CREATE TABLE IF NOT EXISTS clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_type VARCHAR(20) NOT NULL,
    name VARCHAR(200) NOT NULL,
    identification_number VARCHAR(13) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    street VARCHAR(200),
    city VARCHAR(100),
    county VARCHAR(100),
    postal_code VARCHAR(6),
    country VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_clients_identification_number UNIQUE (identification_number),
    CONSTRAINT ck_client_type CHECK (client_type IN ('INDIVIDUAL', 'COMPANY')),
    CONSTRAINT ck_client_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT ck_client_identification_number_not_empty CHECK (LENGTH(TRIM(identification_number)) > 0),
    CONSTRAINT ck_client_email_not_empty CHECK (LENGTH(TRIM(email)) > 0),
    CONSTRAINT ck_client_phone_not_empty CHECK (LENGTH(TRIM(phone)) > 0)
);


CREATE INDEX IF NOT EXISTS idx_clients_identification_number ON clients(identification_number);


CREATE INDEX IF NOT EXISTS idx_clients_name ON clients(name);

CREATE TABLE IF NOT EXISTS identification_number_changes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL,
    old_value VARCHAR(13),
    new_value VARCHAR(13) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(100) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    CONSTRAINT fk_inc_client
        FOREIGN KEY (client_id) REFERENCES clients(id)
        ON DELETE CASCADE,
    CONSTRAINT ck_inc_new_value_not_empty CHECK (LENGTH(TRIM(new_value)) > 0),
    CONSTRAINT ck_inc_changed_by_not_empty CHECK (LENGTH(TRIM(changed_by)) > 0),
    CONSTRAINT ck_inc_reason_not_empty CHECK (LENGTH(TRIM(reason)) > 0)
);


CREATE INDEX IF NOT EXISTS idx_inc_client_id ON identification_number_changes(client_id);


CREATE INDEX IF NOT EXISTS idx_inc_changed_at ON identification_number_changes(changed_at DESC);
