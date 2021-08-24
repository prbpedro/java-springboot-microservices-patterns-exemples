# javaspringbootreactiveapiexemple
This project exemplifies the use of R2DBC in reactive Springboot RESTful API with multiple (read only and write) databases configurations.
The following technologies where used to implement the solution:
- [Java 11](https://openjdk.java.net/projects/jdk/11/)
- [Gradle 7.1](https://docs.gradle.org/7.1/)
- [Springboot 2.5.4](https://docs.spring.io/spring-boot/docs/2.5.4/reference/html/)
- [Docker](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwjhu-fY8MnyAhU-D7kGHeiYAAgQFnoECAYQAw&url=https%3A%2F%2Fwww.docker.com%2F&usg=AOvVaw3p9e1qPvdfjCrUwPYAhUlS)
- [docker-compose](https://docs.docker.com/compose/)
- [R2DBC](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwih95Dx8MnyAhWDHrkGHXQODk8QFnoECAsQAQ&url=https%3A%2F%2Fr2dbc.io%2F&usg=AOvVaw13oUk84LMvVQF4oVez1Ebc)
- [Mysql 5.7] (https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwjTjdn68MnyAhU3HrkGHXWBBKMQFnoECAcQAw&url=https%3A%2F%2Fdev.mysql.com%2Fdownloads%2Fmysql%2F5.7.html&usg=AOvVaw3GW9U8C-zPdNvSVA8ITUkx)
- [com.avast.gradle.docker-compose Gradle plugin](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&ved=2ahUKEwiZ3pGH8cnyAhU0FbkGHTqTDRUQFnoECAIQAQ&url=https%3A%2F%2Fgithub.com%2Favast%2Fgradle-docker-compose-plugin&usg=AOvVaw36MYk3zdQpSAd2rbwLa89a)
- [Vertx.io](https://vertx.io/)
- [Swagger](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwjkz5eL8snyAhWOIbkGHQcQAtkQFnoECAsQAw&url=https%3A%2F%2Fswagger.io%2F&usg=AOvVaw1NniU_dzz5RxjP-3XanWor)

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

This can be achieved by configuring multiple connection factories configured through the R2dbcReadonlyConfiguration and R2dbcWriteConfiguration classes.

To destroy the resources created in the infrastructure initialization run the following command:
```bash
./gradlew runAppInfrastructureComposeDown
```
