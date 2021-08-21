# javaspringbootreactiveapiexemple
This project exemplifies the use of R2DBC in reactive Springboot applications with multiple read and write databases configurations.
The following technologies where used to implement the solution:
- Java 11
- Gradle 7.1
- Springboot 2.5.4
- Docker and docker-compose
- r2dbc
- Mysql 5.7
- com.avast.gradle.docker-compose Gradle plugin

## Running the integration tests

To initialize the infrastructure needed to the tests execute the folloing command:
```bash
./gradlew runAppInfrastructureComposeUp
```
This will create an Instance of MySql with one database named dumb_db, one table named DumbEntity with only only columnd, an auto generated id. This is done by running the scripts / schema.sql file.
The initialization process will also create a read only user by running the scripts / createreadonlyuser.sql file.

After then infrastructure initialization you can run the integration tests by simple running the folloing command:
```bash
./gradlew build
```

The tests ensures that the DumbEntityReadOnlyRepository class does not have write access to the database and that the DumbEntityWriteRepository class does have.

To destroy the resources created in the infrastructure initialization run the folloing command:
```bash
./gradlew runAppInfrastructureComposeDown
```
