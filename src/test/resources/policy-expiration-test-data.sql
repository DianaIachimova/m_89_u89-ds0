INSERT INTO clients (id, client_type, name, identification_number, email, phone, created_at, updated_at) VALUES
('e1e1e1e1-0000-4000-a000-000000000001', 'INDIVIDUAL', 'Expiration Test Client', '1234567890123', 'exp@test.com', '+40700000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO buildings (id, owner_id, street, street_number, city_id, construction_year, building_type, number_of_floors, surface_area, insured_value, flood_zone, earthquake_risk_zone, created_at, updated_at) VALUES
('d1d1d1d1-0000-4000-a000-000000000001', 'e1e1e1e1-0000-4000-a000-000000000001', 'Exp St', '1', 'f1d2c3b4-5a6b-4c8d-9e0f-112233445566', 2020, 'RESIDENTIAL', 2, 100.00, 200000.00, false, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- overdue
INSERT INTO policies (id, policy_number, client_id, building_id, broker_id, currency_id, status, start_date, end_date, base_premium, final_premium, created_at, updated_at) VALUES
('a1a1a1a1-0000-4000-a000-000000000001', 'EXP-TEST-001', 'e1e1e1e1-0000-4000-a000-000000000001', 'd1d1d1d1-0000-4000-a000-000000000001', 'b0b0b0b0-0000-4000-a000-000000000001', 'c0c0c0c0-0000-4000-a000-000000000001', 'ACTIVE', CURRENT_DATE - 31, CURRENT_DATE - 1, 500.00, 550.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- overdue
INSERT INTO policies (id, policy_number, client_id, building_id, broker_id, currency_id, status, start_date, end_date, base_premium, final_premium, created_at, updated_at) VALUES
('a1a1a1a1-0000-4000-a000-000000000002', 'EXP-TEST-002', 'e1e1e1e1-0000-4000-a000-000000000001', 'd1d1d1d1-0000-4000-a000-000000000001', 'b0b0b0b0-0000-4000-a000-000000000001', 'c0c0c0c0-0000-4000-a000-000000000001', 'ACTIVE', CURRENT_DATE - 61, CURRENT_DATE - 2, 600.00, 660.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- not expired
INSERT INTO policies (id, policy_number, client_id, building_id, broker_id, currency_id, status, start_date, end_date, base_premium, final_premium, created_at, updated_at) VALUES
('a1a1a1a1-0000-4000-a000-000000000003', 'EXP-TEST-003', 'e1e1e1e1-0000-4000-a000-000000000001', 'd1d1d1d1-0000-4000-a000-000000000001', 'b0b0b0b0-0000-4000-a000-000000000001', 'c0c0c0c0-0000-4000-a000-000000000001', 'ACTIVE', CURRENT_DATE, CURRENT_DATE + 30, 700.00, 770.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
