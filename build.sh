#!/bin/sh
cd server
rm ~/.m2/settings.xml # Remove default Maven settings since Travis loves to overwrite this
./mvnw package -DskipTests
./mvnw clean test jacoco:report coveralls:report
