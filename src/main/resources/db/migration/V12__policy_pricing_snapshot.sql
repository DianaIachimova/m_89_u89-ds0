CREATE TABLE IF NOT EXISTS policy_pricing_snapshots (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    policy_id       UUID            NOT NULL,
    base_premium    NUMERIC(15, 2)  NOT NULL,
    final_premium   NUMERIC(15, 2)  NOT NULL,
    total_fee_pct   NUMERIC(6, 4)   NOT NULL,
    total_risk_pct  NUMERIC(6, 4)   NOT NULL,
    snapshot_date   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_snapshot_policy UNIQUE (policy_id),
    CONSTRAINT fk_snapshot_policy FOREIGN KEY (policy_id) REFERENCES policies(id)
);

CREATE TABLE IF NOT EXISTS policy_pricing_snapshot_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    snapshot_id     UUID            NOT NULL,
    source_type     VARCHAR(30)     NOT NULL,
    source_id       UUID            NOT NULL,
    name            VARCHAR(200)    NOT NULL,
    percentage      NUMERIC(6, 4)   NOT NULL,
    applied_order   INT             NOT NULL,

    CONSTRAINT fk_item_snapshot FOREIGN KEY (snapshot_id)
        REFERENCES policy_pricing_snapshots(id)
);

CREATE INDEX idx_snapshot_items_snapshot ON policy_pricing_snapshot_items(snapshot_id);
CREATE INDEX idx_snapshot_items_source ON policy_pricing_snapshot_items(source_type, source_id);
