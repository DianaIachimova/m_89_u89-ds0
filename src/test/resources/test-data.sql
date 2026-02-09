-- Countries
INSERT INTO countries (id, name) VALUES
('3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', 'Romania');

-- Counties
INSERT INTO counties (id, country_id, code, name) VALUES
('a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9', '3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', 'B', 'Bucuresti');

-- Cities
INSERT INTO cities (id, county_id, name) VALUES
('f1d2c3b4-5a6b-4c8d-9e0f-112233445566', 'a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9', 'Sector 1');

-- Currencies
INSERT INTO currencies (id, code, name, exchange_rate_to_base, is_active, created_at, updated_at) VALUES
('c0c0c0c0-0000-4000-a000-000000000001', 'RON', 'Romanian Leu', 1.000000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('c0c0c0c0-0000-4000-a000-000000000002', 'EUR', 'Euro', 4.970000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('c0c0c0c0-0000-4000-a000-000000000003', 'USD', 'US Dollar', 4.560000, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Fee configurations
INSERT INTO fee_configurations (id, code, name, type, percentage, effective_from, effective_to, is_active, created_at, updated_at) VALUES
('f0f0f0f0-0000-4000-a000-000000000001', 'ADMIN_FEE', 'Admin Fee', 'ADMIN_FEE', 0.0500, '2025-01-01', '2027-12-31', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('f0f0f0f0-0000-4000-a000-000000000002', 'FLOOD_ZONE', 'Flood Zone Risk Adjustment', 'RISK_ADJUSTMENT', 0.0300, '2025-01-01', '2027-12-31', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('f0f0f0f0-0000-4000-a000-000000000003', 'EARTHQUAKE_ZONE', 'Earthquake Zone Risk Adjustment', 'RISK_ADJUSTMENT', 0.0400, '2025-01-01', '2027-12-31', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Risk factor configurations
INSERT INTO risk_factor_configurations (id, level, reference_id, building_type, adjustment_percentage, is_active, created_at, updated_at) VALUES
('a0a0a0a0-0000-4000-a000-000000000001', 'COUNTRY', '3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', NULL, 0.0200, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('a0a0a0a0-0000-4000-a000-000000000002', 'BUILDING_TYPE', NULL, 'RESIDENTIAL', 0.0100, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Brokers
INSERT INTO brokers (id, broker_code, name, email, phone, status, commission_percentage, created_at, updated_at) VALUES
('b0b0b0b0-0000-4000-a000-000000000001', 'BRK-TEST-001', 'Test Broker Active', 'broker.active@test.com', '+40712345678', 'ACTIVE', 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('b0b0b0b0-0000-4000-a000-000000000002', 'BRK-TEST-002', 'Test Broker Inactive', 'broker.inactive@test.com', '+40712345679', 'INACTIVE', 3.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
