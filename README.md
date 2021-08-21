# javaspringbootreactiveapiexemple
This project exemplifies the use of R2DBC in reactive Springboot applications with multiple (read only and write) databases configurations.
The following technologies where used to implement the solution:
- Java 11
- Gradle 7.1
- Springboot 2.5.4
- Docker and docker-compose
- r2dbc
- Mysql 5.7
- com.avast.gradle.docker-compose Gradle plugin

## Running the integration tests

To initialize the infrastructure needed to the tests execute the following command:
```bash
./gradlew runAppInfrastructureComposeUp
```
This will create an Instance of MySql with one database named dumb_db, one table named DumbEntity with two columns, an auto generated id and a value column. This is done by running the scripts / schema.sql file.
The initialization process will also create a read only user by running the scripts / createreadonlyuser.sql file.

After then infrastructure initialization you can run the integration tests by simple running the following command:
```bash
./gradlew build
```

The tests ensure that the DumbEntityReadOnlyRepository class does not have write access granted to the database and that the DumbEntityWriteRepository class does have.

To destroy the resources created in the infrastructure initialization run the following command:
```bash
./gradlew runAppInfrastructureComposeDown
```
