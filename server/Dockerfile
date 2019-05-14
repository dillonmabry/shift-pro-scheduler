# Build
FROM maven:3.6.1-jdk-8-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

# Run
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY --from=build /home/app/target/shiftpro-0.1.0.BUILD-SNAPSHOT.jar /usr/local/lib/shiftpro.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/shiftpro.jar"]