-- Employees
DROP TABLE employees IF EXISTS;
CREATE TABLE employees (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  email VARCHAR(40),
  phone VARCHAR(30)
);
CREATE INDEX employees_last_name ON employees (last_name);
-- App Users/Administrators
DROP TABLE application_user IF EXISTS;
CREATE TABLE application_user (
  id         INTEGER IDENTITY PRIMARY KEY,
  username VARCHAR(30),
  password  VARCHAR(60)
);
CREATE INDEX application_user_username ON application_user (username);
-- Shifts
DROP TABLE shifts IF EXISTS;
CREATE TABLE shifts (
  id         INTEGER IDENTITY PRIMARY KEY,
  emp_id INTEGER NOT NULL,
  start_time  TIMESTAMP,
  end_time TIMESTAMP
);
ALTER TABLE shifts ADD CONSTRAINT fk_shifts_emp_id FOREIGN KEY (emp_id) REFERENCES employees (id);
CREATE INDEX shifts_emp_id ON shifts (emp_id);
CREATE INDEX shifts_start_time ON shifts (start_time);
CREATE INDEX shifts_end_time ON shifts (end_time);
-- Schedules
DROP TABLE schedules IF EXISTS;
CREATE TABLE schedules (
  id         INTEGER IDENTITY PRIMARY KEY,
  shift_id INTEGER NOT NULL
);
ALTER TABLE schedules ADD CONSTRAINT fk_schedules_emp_id FOREIGN KEY (shift_id) REFERENCES shifts (id);
CREATE INDEX schedules_shift_id ON schedules (shift_id);