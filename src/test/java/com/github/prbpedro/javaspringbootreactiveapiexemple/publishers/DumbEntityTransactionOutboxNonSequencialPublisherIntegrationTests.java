package com.github.prbpedro.javaspringbootreactiveapiexemple.publishers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.IntegrationTestConfiguration;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.concurrent.ExecutionException;

@SpringBootTest
public class DumbEntityTransactionOutboxNonSequencialPublisherIntegrationTests {

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Autowired
    private DumbEntityTransactionOutboxNonSequencialPublisher publisher;

    @Autowired
    private DumbEntityTransactionOutboxWriteRepository repository;

    @BeforeAll
    public static void beforeAll() {
        IntegrationTestConfiguration.configure();
    }

    @BeforeEach
    public void beforeEach() {
        repository.deleteAll().block();
    }

    @Test
    public void publishPendingMessagesTest() throws ExecutionException, InterruptedException {
        DumbEntityTransactionOutbox savedEntityOne = repository.save(
            DumbEntityTransactionOutbox
                .builder()
                .dumbEntityId(1L)
                .generatedUuid("generatedUuid")
                .operation("operation")
                .messageBody("{\"id\":\"1\"}")
                .messageAttributes("{\"id\":\"1\"}")
                .status("PENDING")
                .build())
            .block();

        DumbEntityTransactionOutbox savedEntityTwo = repository.save(
            DumbEntityTransactionOutbox
                .builder()
                .dumbEntityId(1L)
                .generatedUuid("generatedUuid")
                .operation("operation")
                .messageBody("{\"id\":\"1\"}")
                .messageAttributes("{\"id\":\"1\"}")
                .status("PENDING")
                .build())
            .block();

        publisher.publish();

        String entityOneStatus = repository.findById(savedEntityOne.getId()).block().getStatus();
        Assert.isTrue(entityOneStatus.equals("PENDING"), "wrong status for entity");

        String entityTwoStatus = repository.findById(savedEntityTwo.getId()).block().getStatus();
        Assert.isTrue(entityTwoStatus.equals("PENDING"), "wrong status for entity");
    }
}
