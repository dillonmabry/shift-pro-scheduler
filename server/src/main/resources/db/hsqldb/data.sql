-- Administrators
INSERT INTO administrators VALUES (1, 'jdean', 'Jimmy', 'Dean', 'jdean@gmail.com', '704-923-2368');
INSERT INTO administrators VALUES (2, 'mlinda', 'Martha', 'Linda' , 'mlinda@gmail.com', '980-555-4392');

-- Departments
INSERT INTO departments VALUES (1, 1, 'Retail');

-- Employees
INSERT INTO employees VALUES (1, 'jcarter', 'James', 'Carter', 'jcarter@gmail.com', '704-923-4432', 1);
INSERT INTO employees VALUES (2, 'hleary', 'Helen', 'Leary' , 'hleary@gmail.com', '704-923-5092', 1);

-- Shifts
INSERT INTO shifts VALUES (1, '2020-1-10 05:00:00', '2020-1-10 08:00:00');
INSERT INTO shifts VALUES (2, '2020-1-10 05:00:00', '2020-1-10 08:00:00');

-- Assignments (Sample)
INSERT INTO assignments VALUES (1, 1);