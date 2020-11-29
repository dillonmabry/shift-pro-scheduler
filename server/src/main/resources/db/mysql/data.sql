INSERT IGNORE INTO departments VALUES (1, 'Retail');

INSERT IGNORE INTO administrators VALUES (1, 'jdean', 'Jimmy', 'Dean', 'jdean@gmail.com', '704-923-2368', 1);
INSERT IGNORE INTO administrators VALUES (2, 'mlinda', 'Martha', 'Linda' , 'mlinda@gmail.com', '980-555-4392', 1);

INSERT IGNORE INTO employees VALUES (1, 'jcarter', 'James', 'Carter', 'jcarter@gmail.com', '704-923-4432', 1);
INSERT IGNORE INTO employees VALUES (2, 'hleary', 'Helen', 'Leary' , 'hleary@gmail.com', '704-923-5092', 1);
INSERT IGNORE INTO employees VALUES (3, 'jsmith', 'John', 'Smith', 'jsmith@gmail.com', '704-920-3315', 1);
INSERT IGNORE INTO employees VALUES (4, 'athomas', 'Adam', 'Thomas' , 'athomas@gmail.com', '704-920-8883', 1);

INSERT IGNORE INTO shifts VALUES (1, '08:00:00', '16:00:00');
INSERT IGNORE INTO shifts VALUES (2, '16:00:00', '24:00:00');
INSERT IGNORE INTO shifts VALUES (3, '24:00:00', '08:00:00');

INSERT IGNORE INTO assignments VALUES(1, 1, 1, 1);
