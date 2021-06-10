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

CREATE TABLE IF NOT EXISTS shift_days (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  CONSTRAINT shifts_days_check CHECK (
    name = 'Sunday' OR
    name = 'Monday' OR
    name = 'Tuesday' OR
    name = 'Wednesday' OR
    name = 'Thursday' OR
    name = 'Friday' OR
    name = 'Saturday'
  )
) engine=InnoDB;;

CREATE TABLE IF NOT EXISTS assignment_requests (
  id  INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  emp_id  INT(4) UNSIGNED NOT NULL,
  shift_id INT(4) UNSIGNED NOT NULL,
  day_id INT(4) UNSIGNED NOT NULL,
  FOREIGN KEY (emp_id) REFERENCES employees(id),
  FOREIGN KEY (shift_id) REFERENCES shifts(id),
  FOREIGN KEY (day_id) REFERENCES shift_days(id)
) engine=InnoDB;;
