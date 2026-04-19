# Maplewood Course Planning API

Spring Boot REST API (Java 17, SQLite).

## Requirements

- Java 17+
- Maven 3.8+

## Run the server

```bash
mvn spring-boot:run
```

The server starts on **http://localhost:8080**.

## Run the tests

```bash
mvn test
```

## Build a JAR

```bash
mvn package -DskipTests
java -jar target/course-planning-api-1.0.0.jar
```
