#!/bin/sh
cd server
rm ~/.m2/settings.xml
curl -o ~/.m2/settings.xml
./mvnw dependency:go-offline
./mvnw package -DskipTests
./mvnw clean test jacoco:report coveralls:report
