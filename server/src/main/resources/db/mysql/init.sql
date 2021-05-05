CREATE DATABASE IF NOT EXISTS shiftproscheduler;

ALTER DATABASE shiftproscheduler
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

CREATE USER IF NOT EXISTS admin@localhost IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON shiftproscheduler.* TO admin@localhost;

USE shiftproscheduler;

CREATE TABLE IF NOT EXISTS departments (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  dept_name VARCHAR(40) NOT NULL
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS administrators (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(40) NOT NULL,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL,
  email VARCHAR(40) NOT NULL,
  phone VARCHAR(30),
  dept_id INT(4) UNSIGNED,
  FOREIGN KEY (dept_id) REFERENCES departments(id),
  INDEX(last_name),
  INDEX(username)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS employees (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(40) NOT NULL,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL,
  email VARCHAR(40) NOT NULL,
  phone VARCHAR(30),
  dept_id INT(4) UNSIGNED,
  supervisor_id INT(4) UNSIGNED,
  FOREIGN KEY (dept_id) REFERENCES departments(id),
  FOREIGN KEY (supervisor_id) REFERENCES administrators(id),
  INDEX(last_name),
  INDEX(username)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS application_users (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(30) NOT NULL,
  password VARCHAR(60) NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT 0,
  INDEX(username),
  UNIQUE(username)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS confirmation_tokens (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id INT(4) UNSIGNED,
  confirmation_token VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  FOREIGN KEY (user_id) REFERENCES application_users(id),
  INDEX(created_at)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS roles (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  description VARCHAR(60) NOT NULL,
  INDEX(name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS user_roles (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id INT(4) UNSIGNED NOT NULL,
  role_id INT(4) UNSIGNED NOT NULL,
  FOREIGN KEY (user_id) REFERENCES application_users(id),
  FOREIGN KEY (role_id) REFERENCES roles(id),
  UNIQUE(user_id, role_id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS shifts (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  INDEX(start_time),
  INDEX(end_time)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS schedules (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  admin_id INT(4) UNSIGNED NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  is_active BOOLEAN NOT NULL DEFAULT 0,
  INDEX(admin_id),
  INDEX(created_at),
  FOREIGN KEY (admin_id) REFERENCES administrators(id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS assignments (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  emp_id INT(4) UNSIGNED NOT NULL,
  shift_id INT(4) UNSIGNED NOT NULL,
  day_id INT(4) UNSIGNED NOT NULL,
  schedule_id INT(4) UNSIGNED NOT NULL,
  FOREIGN KEY (emp_id) REFERENCES employees(id),
  FOREIGN KEY (shift_id) REFERENCES shifts(id),
  FOREIGN KEY (schedule_id) REFERENCES schedules(id),
  UNIQUE(emp_id, shift_id, day_id, schedule_id)
) engine=InnoDB;

INSERT IGNORE INTO departments VALUES (1, 'Retail');

INSERT IGNORE INTO administrators VALUES (1, 'admin', 'Admin', 'Admin' , 'administrator@gmail.com', '000-000-0000', 1);
INSERT IGNORE INTO administrators VALUES (2, 'jdean', 'Jimmy', 'Dean', 'jdean@gmail.com', '704-923-2368', 1);
INSERT IGNORE INTO administrators VALUES (3, 'mlinda', 'Martha', 'Linda' , 'mlinda@gmail.com', '980-555-4392', 1);

INSERT IGNORE INTO application_users VALUES (1, 'admin', '$2a$10$Y1ZMm7OQpEs5HmwEHc55oeWQa3u8XXFIF18Y5jlCdVi7r8b7iB0E2', 1);

INSERT IGNORE INTO roles VALUES (1, 'USER', 'application user');
INSERT IGNORE INTO roles VALUES (2, 'ADMIN', 'application administrator');

INSERT IGNORE INTO user_roles VALUES (1, 1, 2);

INSERT IGNORE INTO employees VALUES (1, 'jcarter', 'James', 'Carter', 'jcarter@gmail.com', '704-923-4432', 1, 1);
INSERT IGNORE INTO employees VALUES (2, 'hleary', 'Helen', 'Leary' , 'hleary@gmail.com', '704-923-5092', 1, 1);
INSERT IGNORE INTO employees VALUES (3, 'jsmith', 'John', 'Smith', 'jsmith@gmail.com', '704-920-3315', 1, 1);
INSERT IGNORE INTO employees VALUES (4, 'athomas', 'Adam', 'Thomas' , 'athomas@gmail.com', '704-920-8883', 1, 1);

INSERT IGNORE INTO shifts VALUES (1, '08:00:00', '16:00:00');
INSERT IGNORE INTO shifts VALUES (2, '16:00:00', '23:59:59');
INSERT IGNORE INTO shifts VALUES (3, '24:00:00', '08:00:00');
