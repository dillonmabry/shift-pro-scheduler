#!/bin/sh
cd server
curl -o $HOME/.m2/settings.xml
./mvnw dependency:go-offline
./mvnw package -DskipTests
./mvnw clean test jacoco:report coveralls:report
