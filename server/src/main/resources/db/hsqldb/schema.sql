DROP TABLE employees IF EXISTS;

CREATE TABLE employees (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30)
);
CREATE INDEX employees_last_name ON employees (last_name);