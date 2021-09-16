# Shift Pro, Automated Employee Scheduler
[![CircleCI](https://circleci.com/gh/dillonmabry/shift-pro-scheduler.svg?style=shield)](https://app.circleci.com/pipelines/github/dillonmabry/shift-pro-scheduler)
[![codecov](https://codecov.io/gh/dillonmabry/shift-pro-scheduler/branch/master/graph/badge.svg?token=0FMJZSI4IF)](https://codecov.io/gh/dillonmabry/shift-pro-scheduler)

Shift Pro is an open source, automated employee scheduler which can assist your business with correctly assigning employees work shifts throughout the day.

## Development Instructions

### Server (Requires MariaDB, OpenJDK 11, Postfix or local SMTP server)
```
cd server
./mvnw package
java -Dspring.profiles.active=development -jar target/*.jar
```

### SMTP Requirement
Server requires some type of SMTP connection to send client emails. Setup via `application.properties` or production properties for production.

### Local SMTP Configuration using Postfix
- Postfix documentation: https://wiki.archlinux.org/title/postfix#Start_Postfix
- Set SASL Password map with your email/password, update SASL db
- Restart Postfix ```systemctl start postfix```

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

## Contributions
Check latest issues for contributions
