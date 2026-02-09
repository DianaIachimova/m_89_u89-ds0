CREATE TABLE IF NOT EXISTS administrators (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_administrators_email UNIQUE (email),
    CONSTRAINT ck_administrators_name_not_empty CHECK (LENGTH(TRIM(name)) >= 2),
    CONSTRAINT ck_administrators_email_not_empty CHECK (LENGTH(TRIM(email)) > 0),
    CONSTRAINT ck_administrators_role CHECK (role IN ('ADMIN', 'MANAGER'))
    );

CREATE UNIQUE INDEX idx_administrators_email_ci ON administrators (LOWER(email));
CREATE INDEX idx_administrators_role ON administrators (role);
