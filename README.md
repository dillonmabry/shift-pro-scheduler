# Shift Pro, Automated Employee Scheduler
[![Build Status](https://travis-ci.org/dillonmabry/shift-pro-scheduler.svg?branch=master)](https://travis-ci.org/dillonmabry/shift-pro-scheduler)
[![Coverage Status](https://coveralls.io/repos/github/dillonmabry/shift-pro-scheduler/badge.svg?branch=master)](https://coveralls.io/github/dillonmabry/shift-pro-scheduler?branch=master)

Shift Pro is an automated shift scheduling system which can assist your business with correctly assigning employees work shifts throughout the day.

## Install Instructions
```
./mvnw package
java -jar target/*.jar
```

## To-Do
- [X] Create initial repo with basic spring project setup, tests, and basic CI
- [ ] Create entity relation models and basic schema setup
- [ ] Create scheduling algorithm to schedule shifts based on preferences
- [ ] Seed/setup database model
- [ ] Create services/workflows for running scheduling algorithm, queue basis? micro-task framework?
- [ ] Setup server services and APIs
- [ ] Create basic login/authentication for app
- [ ] Create shift scheduling portal for app, for administrators
- [ ] Future: Ability for employees to set shift preferences
