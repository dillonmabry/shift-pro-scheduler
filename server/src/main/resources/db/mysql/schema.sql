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

CREATE TABLE IF NOT EXISTS employees (
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

CREATE TABLE IF NOT EXISTS application_user (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(30) NOT NULL,
  password VARCHAR(60) NOT NULL,
  INDEX(username)
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
  created_at DATE NOT NULL,
  is_active BIT NOT NULL DEFAULT 0, 
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
