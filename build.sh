#!/bin/bash
cd server
./mvnw package
./mvnw clean test jacoco:report coveralls:report