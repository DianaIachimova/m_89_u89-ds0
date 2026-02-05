-- Countries
INSERT INTO countries (id, name) VALUES
('3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', 'Romania');

-- Counties
INSERT INTO counties (id, country_id, code, name) VALUES
('a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9', '3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', 'B', 'Bucuresti');

-- Cities
INSERT INTO cities (id, county_id, name) VALUES
('f1d2c3b4-5a6b-4c8d-9e0f-112233445566', 'a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9', 'Sector 1');
