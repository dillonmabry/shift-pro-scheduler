INSERT IGNORE INTO departments VALUES (1, 'Retail');

INSERT IGNORE INTO administrators VALUES (1, 'admin', 'Admin', 'Admin' , 'administrator@gmail.com', '000-000-0000', 1);
INSERT IGNORE INTO administrators VALUES (2, 'jdean', 'Jimmy', 'Dean', 'jdean@gmail.com', '704-923-2368', 1);
INSERT IGNORE INTO administrators VALUES (3, 'mlinda', 'Martha', 'Linda' , 'mlinda@gmail.com', '980-555-4392', 1);

INSERT IGNORE INTO application_user VALUES (1, 'admin', '$2a$10$Y1ZMm7OQpEs5HmwEHc55oeWQa3u8XXFIF18Y5jlCdVi7r8b7iB0E2');

INSERT IGNORE INTO employees VALUES (1, 'jcarter', 'James', 'Carter', 'jcarter@gmail.com', '704-923-4432', 1);
INSERT IGNORE INTO employees VALUES (2, 'hleary', 'Helen', 'Leary' , 'hleary@gmail.com', '704-923-5092', 1);
INSERT IGNORE INTO employees VALUES (3, 'jsmith', 'John', 'Smith', 'jsmith@gmail.com', '704-920-3315', 1);
INSERT IGNORE INTO employees VALUES (4, 'athomas', 'Adam', 'Thomas' , 'athomas@gmail.com', '704-920-8883', 1);

INSERT IGNORE INTO shifts VALUES (1, '08:00:00', '16:00:00');
INSERT IGNORE INTO shifts VALUES (2, '16:00:00', '24:00:00');
INSERT IGNORE INTO shifts VALUES (3, '24:00:00', '08:00:00');
