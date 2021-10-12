package com.github.prbpedro.javaspringbootreactiveapiexemple.publishers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.IntegrationTestConfiguration;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import com.github.prbpedro.javaspringbootreactiveapiexemple.services.DumbEntityTransactionOutboxNonSequencialPublisherService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.concurrent.ExecutionException;

@SpringBootTest
public class DumbEntityTransactionOutboxNonSequencialPublisherTransactionalIntegrationTests {

    @MockBean
    private SnsAsyncClient snsAsyncClient;

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
    public void ensuringTransactionalContext() {
        DumbEntityTransactionOutbox savedEntity = repository.save(
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

        Mockito.when(snsAsyncClient.publish(Mockito.any(PublishRequest.class))).thenThrow(new RuntimeException("TestException"));

        RuntimeException thrownException = null;
        try {
            publisher.publish();
        } catch (RuntimeException ex) {
            thrownException = ex;
        }

        Assert.isTrue(thrownException != null, "wrong thrown exception");
        Assert.isTrue(thrownException.getMessage().equals("TestException"), "wrong thrown exception");

        String entityStatus = repository.findById(savedEntity.getId()).block().getStatus();
        Assert.isTrue(entityStatus.equals("PENDING"), "wrong status for entity");
    }
}
