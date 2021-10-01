# Shift Pro, Automated Employee Scheduler
[![CircleCI](https://circleci.com/gh/dillonmabry/shift-pro-scheduler.svg?style=shield)](https://app.circleci.com/pipelines/github/dillonmabry/shift-pro-scheduler)
[![codecov](https://codecov.io/gh/dillonmabry/shift-pro-scheduler/branch/master/graph/badge.svg?token=0FMJZSI4IF)](https://codecov.io/gh/dillonmabry/shift-pro-scheduler)

Shift Pro is an open source, automated employee scheduler which can assist your business with correctly assigning employees work shifts throughout the day.

## Development Instructions

### Server (Requires MariaDB, OpenJDK 11, Postfix or local SMTP server)
- ```application.properties``` for generic app properties
- ```application-development.properties``` for setting up development properties
- ```application-production.properties``` for setting up production properties
```
cd server
./mvnw package
java -Dspring.profiles.active=development -jar target/*.jar
```

### Local SMTP Configuration using Postfix
- Postfix documentation: https://wiki.archlinux.org/title/postfix#Start_Postfix
- Set SASL Password map with your email/password, update SASL db
- Restart Postfix ```systemctl start postfix```

### Client
In ./client setup ```.env``` file with the following (replace with your API url):
```
REACT_APP_API_URL=http://localhost:8080/api
```
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
For production/docker setup create a ```.env``` file or set environment variables per following:
```
MYSQL_DATABASE=shiftproscheduler
MYSQL_ROOT_PASSWORD=<password>
MYSQL_USER=<user>
MYSQL_PASSWORD=<password>
SPRING_DATASOURCE_URL=jdbc:mysql://shiftpro_mysql:3306/shiftproscheduler
SPRING_DATASOURCE_USER_NAME=<user>
SPRING_DATASOURCE_PASSWORD=<password>
```
```
docker-compose build
docker-compose up
```

## Contributions
Check latest issues for contributions
