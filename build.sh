#!/bin/bash
cd server
./mvnw package
./mvnw test
./mvnw clean test jacoco:report coveralls:report