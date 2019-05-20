CREATE DATABASE IF NOT EXISTS shiftproscheduler;

ALTER DATABASE shiftproscheduler
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

CREATE USER IF NOT EXISTS 'admin'@'localhost' IDENTIFIED WITH mysql_native_password BY 'admin';
GRANT ALL PRIVILEGES ON shiftproscheduler.* TO'admin'@'localhost';

USE shiftproscheduler;

CREATE TABLE IF NOT EXISTS employees (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL,
  email VARCHAR(40) NOT NULL,
  phone VARCHAR(30),
  INDEX(last_name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS application_user (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(30) NOT NULL,
  password VARCHAR(60) NOT NULL,
  INDEX(username)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS shifts (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  emp_id INT(4) UNSIGNED NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  FOREIGN KEY (emp_id) REFERENCES employees(id),
  INDEX(emp_id),
  INDEX(start_time),
  INDEX(end_time),
  UNIQUE(id, emp_id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS schedules (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,
  shift_id INT(4) UNSIGNED NOT NULL,
  FOREIGN KEY (shift_id) REFERENCES shifts(id),
  INDEX(shift_id),
  UNIQUE(id, shift_id),
  PRIMARY KEY (id, shift_id)
) engine=InnoDB;
