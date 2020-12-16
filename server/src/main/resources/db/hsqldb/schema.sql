-- Departments
DROP TABLE departments IF EXISTS;
CREATE TABLE departments (
  id         INTEGER IDENTITY PRIMARY KEY,
  dept_name  VARCHAR(40)
);

-- Administrators
DROP TABLE administrators IF EXISTS;
CREATE TABLE administrators (
  id         INTEGER IDENTITY PRIMARY KEY,
  username VARCHAR(40),
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  email VARCHAR(40),
  phone VARCHAR(30),
  dept_id INTEGER
);
CREATE INDEX administrators_dept_id ON administrators (dept_id);
CREATE INDEX administrators_username ON administrators (username);
CREATE INDEX administrators_last_name ON administrators (last_name);
ALTER TABLE administrators ADD CONSTRAINT fk_administrators_dept_id FOREIGN KEY (dept_id) REFERENCES departments (id);

-- Employees
DROP TABLE employees IF EXISTS;
CREATE TABLE employees (
  id         INTEGER IDENTITY PRIMARY KEY,
  username VARCHAR(40),
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  email VARCHAR(40),
  phone VARCHAR(30),
  dept_id INTEGER,
  supervisor_id INTEGER
);
CREATE INDEX employees_dept_id ON employees (dept_id);
CREATE INDEX employees_last_name ON employees (last_name);
CREATE INDEX employees_username ON employees (username);
ALTER TABLE employees ADD CONSTRAINT fk_employees_dept_id FOREIGN KEY (dept_id) REFERENCES departments (id);
ALTER TABLE employees ADD CONSTRAINT fk_employees_supervisor_id FOREIGN KEY (supervisor_id) REFERENCES administrators (id);

-- App users
DROP TABLE application_users IF EXISTS
CREATE TABLE application_users (
  id         INTEGER IDENTITY PRIMARY KEY,
  username VARCHAR(40),
  password  VARCHAR(60)
);
CREATE INDEX application_users_username ON application_users (username);
CREATE UNIQUE INDEX application_users_un_username ON application_users (username);

-- Roles
DROP TABLE roles IF EXISTS 
CREATE TABLE roles (
  id INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(30),
  description VARCHAR(60)
);
CREATE INDEX roles_name ON roles (name);

-- User Roles
DROP TABLE user_roles IF EXISTS 
CREATE TABLE user_roles (
  id INTEGER IDENTITY PRIMARY KEY,
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL
);
ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES application_users (id);
ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id) REFERENCES roles (id);
CREATE UNIQUE INDEX user_roles_user_id_role_id ON user_roles (user_id, role_id);

-- Shifts
DROP TABLE shifts IF EXISTS;
CREATE TABLE shifts (
  id         INTEGER IDENTITY PRIMARY KEY,
  start_time  TIME,
  end_time TIME
);
CREATE INDEX shifts_id ON shifts (id);
CREATE INDEX shifts_start_time ON shifts (start_time);
CREATE INDEX shifts_end_time ON shifts (end_time);

-- Schedules
DROP TABLE schedules IF EXISTS; 
CREATE TABLE schedules (
  id INTEGER IDENTITY PRIMARY KEY,
  admin_id INTEGER NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  is_active BOOLEAN DEFAULT FALSE NOT NULL,
);
CREATE INDEX schedules_admin_id ON schedules (admin_id);
CREATE INDEX schedules_created_at ON schedules (created_at);
ALTER TABLE schedules ADD CONSTRAINT fk_schedules_admin_id FOREIGN KEY (admin_id) REFERENCES administrators (id);

-- Assignments
DROP TABLE assignments IF EXISTS;
CREATE TABLE assignments (
  id  INTEGER IDENTITY PRIMARY KEY,
  emp_id  INTEGER NOT NULL,
  shift_id INTEGER NOT NULL,
  day_id INTEGER NOT NULL,
  schedule_id INTEGER NOT NULL
);
ALTER TABLE assignments ADD CONSTRAINT fk_assignments_emp_id FOREIGN KEY (emp_id) REFERENCES employees (id);
ALTER TABLE assignments ADD CONSTRAINT fk_assignments_shift_id FOREIGN KEY (shift_id) REFERENCES shifts (id);
ALTER TABLE assignments ADD CONSTRAINT fk_assignments_schedule_id FOREIGN KEY (schedule_id) REFERENCES schedules (id);
CREATE INDEX assignments_shift_id ON assignments (shift_id);
CREATE INDEX assignments_emp_id ON assignments (emp_id);
CREATE INDEX assignments_schedule_id ON assignments (schedule_id);
CREATE UNIQUE INDEX assignments_emp_shift_day ON assignments (emp_id, shift_id, day_id, schedule_id);
