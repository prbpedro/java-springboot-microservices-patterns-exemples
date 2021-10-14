# java-springboot-microservices-patterns-exemples

This project implements the Messaging, Database Per Service, CQRS, Domain Event, Transactional Outbox, Polling Publisher, and Idempotent Consumer microservices patterns described in [microservices.io](https://microservices.io) using the following technologies:
- [openjdk-11](https://openjdk.java.net/projects/jdk/11/)
- [gradle-7.1](https://docs.gradle.org/7.1/)
- [spring-boot-2.5.4](https://docs.spring.io/spring-boot/docs/2.5.4/reference/html/)
- [docker](https://www.docker.com/)
- [docker-compose](https://docs.docker.com/compose/)
- [r2dbc](https://r2dbc.io/)
- [mysql-5.7](https://dev.mysql.com/downloads/mysql/5.7.html)
- [gradle-docker-compose-plugin](https://github.com/avast/gradle-docker-compose-plugin)
- [vertx](https://vertx.io/)
- [swagger](https://swagger.io/)
- [localstack](https://github.com/localstack/localstack)
- [spring-cloud-starter-aws-messaging](https://github.com/awspring/spring-cloud-aws)
- [aws-java-sdk](https://docs.aws.amazon.com/sdk-for-java/index.html)

## Implemented Patterns
- [Messaging](https://microservices.io/patterns/communication-style/messaging.html)
  - This pattern is implemented through the publication of events generated in a RESTful API in an Aws SNS Topic, sending them to an Aws SQS Queue through topic subscription and consumption of these messages by the application. The following classes implements this pattern:
      - AwsConfig
      - DumbEntityTransactionOutboxNonSequencialPublisher
      - DumbQueueConsumer
- [Database Per Service](https://microservices.io/patterns/data/database-per-service.html)
  - This pattern is implemented through the use of a single database for writing operations by the application.
- [CQRS](https://microservices.io/patterns/data/cqrs.html)
  - This pattern is implemented through the use of two different connections to the database, one with only read data, and the other with read and write access. The following classes implements this pattern:
      - DumbEntityReadOnlyRepository
      - SecondDumbEntityReadOnlyRepository
      - DumbEntityTransactionOutboxWriteRepository
      - DumbEntityWriteRepository
      - SecondDumbEntityWriteRepository
      - R2dbcReadonlyConfiguration
      - R2dbcWriteConfiguration
- [Domain Event](https://microservices.io/patterns/data/domain-event.html)
    - This pattern is implemented by sending domain events about the management of the main entity of the project's RESTFul endpoints. The following classes implements this pattern:
      - DumbEntityController
      - DumbEntityTransactionalService
      - DumbEntityTransactionOutboxNonSequencialPublisher
      - DumbEntityWriteRepository
      - DumbEntityTransactionOutboxWriteRepository
- [Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html)
  - This pattern is implemented by persisting the event to be published in the Aws SNS Topic in a database table in the same transaction that generates the domain event related to the event to be sent.  The following classes implements this pattern:
      - DumbEntityController
      - DumbEntityTransactionalService
      - DumbEntityTransactionOutboxWriteRepository
- [Polling Publisher](https://microservices.io/patterns/data/polling-publisher.html)
  - This pattern is implemented through a timed process that publishes the events in an Aws SNS Topic obtained from a database table. The following classes implements this pattern:
      - DumbEntityTransactionOutboxNonSequencialPublisher
      - DumbEntityTransactionOutboxWriteRepository
- [Idempotent Consumer](https://microservices.io/patterns/communication-style/idempotent-consumer.html) 
  - This pattern is implemented through the consumption of messages received in the Aws SQS Queue in order to guarantee that each one will only be processed once. The following classes implements this pattern:
      - DumbQueueConsumer
      - SecondDumbEntityWriteRepository

## Initializing the infrastructure

To initialize the infrastructure needed to the tests and the application, execute the following command:
```bash
./gradlew runAppInfrastructureComposeUp
```
This will create an Instance of MySql with one database named dumb_db, one table named DumbEntity with two columns, an auto generated id and a value column. This is done by running the scripts / schema.sql file.
The initialization process will also create a read only user by running the scripts / createreadonlyuser.sql file.

A localstack container will also be created to simulate the needed AWS resources. The AWS resources are created if they not exist in the initialization of the application through the AwsConfig configuration class.

To destroy the resources created in the infrastructure initialization run the following command:
```bash
./gradlew runAppInfrastructureComposeDown
```

## Configuring the application for integration tests and run
The application is automatically configured to run and for the integration tests through the build.gradle configuration file.
The following environment variables are configured:
  - JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_SERVICE_HOST
  - JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_SERVICE_PORT
  - JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_SERVICE_DATABASE
  - JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_READONLY_SERVICE_USER
  - JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_READONLY_SERVICE_PASSWORD
  - JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_WRITE_SERVICE_USER
  - JAVA_SPRINGBOOT_REACTIVE_API_EXEMPLE_MYSQL_WRITE_SERVICE_PASSWORD
  - AWS_SNS_ENDPOINT
  - AWS_SQS_ENDPOINT
  - AWS_ACCESS_KEY_ID
  - AWS_SECRET_ACCESS_KEY
  - AWS_REGION

## Running the integration tests
After then infrastructure initialization you can run the integration tests by simple running the following command:
```bash
./gradlew build
```

## Running the application
After then infrastructure initialization you can run the application executing the following command:
```bash
./gradlew bootRun
```

The application will expose 4 endpoints:
- Dumb entity PUT
  - This endpoint persists a new or already saved DumbEntity entity, and persists a new DumbEntityTransactionOutbox entity within the same bank transaction
- Dumb entity GET
  - This endpoint gets a DumbEntity register
- Dumb entity LIST
  - This endpoint lists DumbEntity registers
- Dumb entity DELETE
  - This endpoint deletes the DumbEntity entity and persist a new DumbEntityTransactionOutbox entity within the same bank transaction
  
To call the exposed endpoints, you can access the swagger-ui page via the address http://localhost:8080/swagger-ui.html in the browser.

The application configures a scheduler that run every 10 seconds afters the finish of the last execution to get the DumbEntityTransactionOutbox entities not sent to the SNS Topic. DumbEntityTransactionOutboxNonSequencialPublisher class.

The application also configures an idempotent SQS Queue Consumer to the events published to the SNS Topic and sent to the Sqs Queue through topic subscription. DumbQueueConsumer class.
