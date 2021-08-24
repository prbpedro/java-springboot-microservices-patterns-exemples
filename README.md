# javaspringbootreactiveapiexemple
This project exemplifies the use of R2DBC in reactive Springboot RESTful API with multiple (read only and write) databases configurations.
The following technologies where used to implement the solution:
- [Java 11](https://openjdk.java.net/projects/jdk/11/)
- [Gradle 7.1](https://docs.gradle.org/7.1/)
- [Springboot 2.5.4](https://docs.spring.io/spring-boot/docs/2.5.4/reference/html/)
- [Docker](https://www.docker.com/)
- [docker-compose](https://docs.docker.com/compose/)
- [R2DBC](https://r2dbc.io/)
- [Mysql 5.7](https://dev.mysql.com/downloads/mysql/5.7.html)
- [com.avast.gradle.docker-compose Gradle plugin](https://github.com/avast/gradle-docker-compose-plugin)
- [Vertx.io](https://vertx.io/)
- [Swagger](https://swagger.io/)

## Initializing the infrastructure

To initialize the infrastructure needed to the tests and the application, execute the following command:
```bash
./gradlew runAppInfrastructureComposeUp
```
This will create an Instance of MySql with one database named dumb_db, one table named DumbEntity with two columns, an auto generated id and a value column. This is done by running the scripts / schema.sql file.
The initialization process will also create a read only user by running the scripts / createreadonlyuser.sql file.


To destroy the resources created in the infrastructure initialization run the following command:
```bash
./gradlew runAppInfrastructureComposeDown
```

## Running the integration tests
After then infrastructure initialization you can run the integration tests by simple running the following command:
```bash
./gradlew build
```

The tests ensure that the DumbEntityReadOnlyRepository class does not have write access granted to the database and that the DumbEntityWriteRepository class does have.

This can be achieved by configuring multiple connection factories through the R2dbcReadonlyConfiguration and R2dbcWriteConfiguration classes.


## Runnig the application
After then infrastructure initialization you can run the application executing the following command:
```bash
./gradlew bootRun
```

The application will expose 4 endpoints:
- Dumb entity PUT
- Dumb entity GET
- Dumb entity LIST
- Dumb entity DELETE

To call the exposed endpoints, you can access the swagger-ui page via the address http://localhost:8080/swagger-ui in the browser.
