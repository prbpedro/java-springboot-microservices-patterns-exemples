package com.github.prbpedro.javaspringbootreactiveapiexemple.publishers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.config.AwsConfig;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class DumbEntityTransactionOutboxNonSequencialPublisherTransactionalIntegrationTests {

    @MockBean
    private SnsAsyncClient snsAsyncClient;

    @Autowired
    private DumbEntityTransactionOutboxNonSequencialPublisher publisher;

    @Autowired
    private DumbEntityTransactionOutboxWriteRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository.deleteAll().block();
    }

    @Test
    public void ensuringTransactionalContext() {
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
                .dumbEntityId(2L)
                .generatedUuid("generatedUuid2")
                .operation("operation2")
                .messageBody("{\"id\":\"2\"}")
                .messageAttributes("{\"id\":\"2\"}")
                .status("PENDING")
                .build())
            .block();

        Mockito.when(
            snsAsyncClient.publish(
                PublishRequest
                    .builder()
                    .topicArn(AwsConfig.getDumbTopicArn())
                    .message(savedEntityOne.getMessageBody())
                    .messageAttributes(savedEntityOne.buildMessageAttributesMap())
                    .build()))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("TestException")));

        CompletableFuture cf = CompletableFuture.completedFuture(
            PublishResponse
                .builder()
                .sdkHttpResponse(
                    SdkHttpResponse
                        .builder()
                        .statusCode(200)
                        .build())
                .build());

        Mockito.when(
            snsAsyncClient.publish(
                PublishRequest
                    .builder()
                    .topicArn(AwsConfig.getDumbTopicArn())
                    .message(savedEntityTwo.getMessageBody())
                    .messageAttributes(savedEntityTwo.buildMessageAttributesMap())
                    .build()))
            .thenReturn(cf);

        publisher.publish();

        String entityOneStatus = repository.findById(savedEntityOne.getId()).block().getStatus();
        Assert.isTrue(entityOneStatus.equals("PENDING"), "wrong status for entity");

        String entityOneStatusTwo = repository.findById(savedEntityTwo.getId()).block().getStatus();
        Assert.isTrue(entityOneStatusTwo.equals("PENDING"), "wrong status for entity");
    }
}
