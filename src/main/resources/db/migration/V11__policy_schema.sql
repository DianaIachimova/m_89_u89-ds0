CREATE TABLE IF NOT EXISTS policies (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    policy_number       VARCHAR(30)     NOT NULL,
    client_id           UUID            NOT NULL,
    building_id         UUID            NOT NULL,
    broker_id           UUID            NOT NULL,
    currency_id         UUID            NOT NULL,
    status              VARCHAR(15)     NOT NULL DEFAULT 'DRAFT',
    start_date          DATE            NOT NULL,
    end_date            DATE            NOT NULL,
    base_premium        NUMERIC(15, 2)  NOT NULL,
    final_premium       NUMERIC(15, 2)  NOT NULL,
    cancelled_at        DATE,
    cancellation_reason VARCHAR(500),
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_policies_number UNIQUE (policy_number),
    CONSTRAINT fk_policies_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_policies_building FOREIGN KEY (building_id) REFERENCES buildings(id),
    CONSTRAINT fk_policies_broker FOREIGN KEY (broker_id) REFERENCES brokers(id),
    CONSTRAINT fk_policies_currency FOREIGN KEY (currency_id) REFERENCES currencies(id),
    CONSTRAINT ck_policies_status CHECK (status IN ('DRAFT', 'ACTIVE', 'EXPIRED', 'CANCELLED')),
    CONSTRAINT ck_policies_dates CHECK (end_date > start_date),
    CONSTRAINT ck_policies_base_premium CHECK (base_premium > 0),
    CONSTRAINT ck_policies_final_premium CHECK (final_premium > 0)
);

CREATE INDEX idx_policies_client ON policies(client_id);
CREATE INDEX idx_policies_building ON policies(building_id);
CREATE INDEX idx_policies_broker ON policies(broker_id);
CREATE INDEX idx_policies_currency ON policies(currency_id);
CREATE INDEX idx_policies_status ON policies(status);
CREATE INDEX idx_policies_dates ON policies(start_date, end_date);
