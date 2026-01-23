-- Countries
INSERT INTO countries (id, name) VALUES
('3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', 'Romania'),
('8a2b7c4e-1c9d-4f8a-9a1a-2c0f6c4b9d77', 'Moldova');

-- Counties
INSERT INTO counties (id, country_id, code, name) VALUES
-- Romania
('a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9', '3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', 'B', 'Bucuresti'),
('c2c0b1e2-5f77-4c4d-9a45-1b0d7d0a44c1', '3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', 'CJ', 'Cluj'),
('0c6f7b2a-2f5a-4e9b-8f6b-9c0b0e7f6e3a', '3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', 'IS', 'Iasi'),
('7d1a4d2f-6f9d-4b2d-a1f3-2d9f6c0d8c55', '3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11', 'TM', 'Timis'),

-- Moldova
('e9c4a5f1-2a6c-4b2e-9c1a-3a2c8e1f5a12', '8a2b7c4e-1c9d-4f8a-9a1a-2c0f6c4b9d77', 'C', 'Chisinau'),
('4b7a2c1d-8e3f-4c2a-9d0c-6d2a1f8c3b91', '8a2b7c4e-1c9d-4f8a-9a1a-2c0f6c4b9d77', 'BL', 'Balti'),
('2f8c1a6e-5b7d-4c1a-8c2d-9e1f3a6b7c21', '8a2b7c4e-1c9d-4f8a-9a1a-2c0f6c4b9d77', 'CAH', 'Cahul');

-- Cities
INSERT INTO cities (id, county_id, name) VALUES
-- Bucuresti
('f1d2c3b4-5a6b-4c8d-9e0f-112233445566', 'a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9', 'Sector 1'),
('aa6b5c4d-3e2f-4a1b-9c8d-776655443322', 'a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9', 'Sector 2'),
('0b1c2d3e-4f5a-4b6c-8d9e-aabbccddeeff', 'a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9', 'Sector 3'),

-- Cluj
('1a2b3c4d-5e6f-4a8b-9c0d-1e2f3a4b5c6d', 'c2c0b1e2-5f77-4c4d-9a45-1b0d7d0a44c1', 'Cluj-Napoca'),
('6d5c4b3a-2f1e-4d0c-9b8a-7f6e5d4c3b2a', 'c2c0b1e2-5f77-4c4d-9a45-1b0d7d0a44c1', 'Turda'),
('9f8e7d6c-5b4a-4c3d-8e7f-6a5b4c3d2e1f', 'c2c0b1e2-5f77-4c4d-9a45-1b0d7d0a44c1', 'Dej'),

-- Iasi
('3c2b1a0f-9e8d-4c7b-8a9b-0c1d2e3f4a5b', '0c6f7b2a-2f5a-4e9b-8f6b-9c0b0e7f6e3a', 'Iasi'),
('5b4a3c2d-1e0f-4b8a-9c7d-6e5f4a3b2c1d', '0c6f7b2a-2f5a-4e9b-8f6b-9c0b0e7f6e3a', 'Pascani'),

-- Timis
('7a6b5c4d-3e2f-4d1c-8b9a-0f1e2d3c4b5a', '7d1a4d2f-6f9d-4b2d-a1f3-2d9f6c0d8c55', 'Timisoara'),
('2d3c4b5a-6f7e-4d8c-9b0a-1c2d3e4f5a6b', '7d1a4d2f-6f9d-4b2d-a1f3-2d9f6c0d8c55', 'Lugoj'),

-- Chisinau
('c0ffee00-1111-4a2b-9c3d-4e5f6a7b8c9d', 'e9c4a5f1-2a6c-4b2e-9c1a-3a2c8e1f5a12', 'Chisinau'),
('deadc0de-2222-4b3c-8d9e-1a2b3c4d5e6f', 'e9c4a5f1-2a6c-4b2e-9c1a-3a2c8e1f5a12', 'Durlesti'),
('b16b00b5-3333-4c4d-9e0f-7a6b5c4d3e2f', 'e9c4a5f1-2a6c-4b2e-9c1a-3a2c8e1f5a12', 'Codru'),

-- Balti
('4e3d2c1b-0a9f-4e8d-8c7b-6a5b4c3d2e1a', '4b7a2c1d-8e3f-4c2a-9d0c-6d2a1f8c3b91', 'Balti'),
('1e2d3c4b-5a6f-4b7c-8d9e-0f1a2b3c4d5e', '4b7a2c1d-8e3f-4c2a-9d0c-6d2a1f8c3b91', 'Singerei'),

-- Cahul
('6f5e4d3c-2b1a-4c9d-8e7f-0a1b2c3d4e5f', '2f8c1a6e-5b7d-4c1a-8c2d-9e1f3a6b7c21', 'Cahul'),
('0a1b2c3d-4e5f-4a6b-8c9d-1e2f3a4b5c6e', '2f8c1a6e-5b7d-4c1a-8c2d-9e1f3a6b7c21', 'Giurgiulesti');
