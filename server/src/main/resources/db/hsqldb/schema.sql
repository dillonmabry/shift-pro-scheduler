DROP TABLE employees IF EXISTS;

CREATE TABLE employees (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  email VARCHAR(40),
  phone VARCHAR(30)
);
CREATE INDEX employees_last_name ON employees (last_name);
CREATE TABLE application_user (
  id         INTEGER IDENTITY PRIMARY KEY,
  username VARCHAR(30),
  password  VARCHAR(60)
);
CREATE INDEX application_user_username ON application_user (username);