# Shift Pro, Automated Employee Scheduler
[![CircleCI](https://circleci.com/gh/dillonmabry/shift-pro-scheduler.svg?style=shield)](https://app.circleci.com/pipelines/github/dillonmabry/shift-pro-scheduler)

Shift Pro is an open source, automated employee scheduler which can assist your business with correctly assigning employees work shifts throughout the day.

## Development Instructions

### Server (Requires MariaDB, OpenJDK 11, Postfix or local SMTP server)
```
cd server
./mvnw package
java -Dspring.profiles.active=development -jar target/*.jar
```
### Installing Postfix or local SMTP
Server requires some type of SMTP connection to send client emails. Setup via `application.properties` or production properties for production.

### Client
```
cd client
npm install
npm run start
```
#### Linting/Style
```
npm run lint
npm run lint-fix
npm run format
```

## Docker Instructions
```
docker-compose build
docker-compose up
```

## To-Do
- [X] Create initial repo with basic spring project setup, tests, and basic CI
- [X] Create entity relation models and basic schema setup
- [X] Create basic scheduling algorithm to schedule shifts based on preferences
- [X] Seed/setup database model
- [X] Create services/workflows for running scheduling algorithm, queue basis? micro-task framework?
- [X] Setup server services and APIs
- [X] Create basic login/authentication for app
- [ ] Create shift scheduling portal for app, for administrators and employees
- [ ] Create profile area for employees/administrators
- [ ] Create area to add new employees
- [ ] Future: Ability for employees to set shift preferences
- [ ] Future: add email and SMS notifications for employees
