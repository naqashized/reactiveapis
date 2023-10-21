# reactiveapis
This is rather simple Webflux app using latest versions i.e Java 21 and Springboot 3.
This app uses Postgres as backend DB and flyway for migrations.This uses docker-compose to spin postgres container. Tests are using testcontainers
## Running the app
Clone the repository and go to the app directory.And run below command;

docker-compose up

gradle build or ./gradlew build

To run the tests, use below command;

gradle test or ./gradlew test

To run app from the IDE, you need to set local as active profile
otherwise make the local profile active in application.yaml like mentioned below(not recommended);

```
spring:
  profiles:
    active: local
```
