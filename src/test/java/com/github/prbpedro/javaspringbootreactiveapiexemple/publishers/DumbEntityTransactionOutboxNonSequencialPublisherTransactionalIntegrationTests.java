package com.github.prbpedro.javaspringbootreactiveapiexemple.publishers;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.github.prbpedro.javaspringbootreactiveapiexemple.config.AwsConfig;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;

@SpringBootTest
public class DumbEntityTransactionOutboxNonSequencialPublisherTransactionalIntegrationTests {

    @MockBean
    private AmazonSNSAsync amazonSNSAsync;

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
                .messageAttributes("{\"uuid\":\"1\", \"operation\":\"1\"}")
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
                .messageAttributes("{\"uuid\":\"2\", \"operation\":\"2\"}")
                .status("PENDING")
                .build())
            .block();

        PublishRequest p = new PublishRequest();
        p.setTopicArn(AwsConfig.getDumbTopicArn());
        p.setMessage(savedEntityOne.getMessageBody());
        p.setMessageAttributes(savedEntityOne.buildMessageAttributesMap());

        PublishResult r = Mockito.mock(PublishResult.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(httpResponse.getStatusCode()).thenReturn(200);
        SdkHttpMetadata sdkHttpMetadata = SdkHttpMetadata.from(httpResponse);
        Mockito.when(r.getSdkHttpMetadata()).thenReturn(sdkHttpMetadata);

        PublishRequest p2 = new PublishRequest();
        p.setTopicArn(AwsConfig.getDumbTopicArn());
        p.setMessage(savedEntityTwo.getMessageBody());
        p.setMessageAttributes(savedEntityTwo.buildMessageAttributesMap());

        Mockito.when(amazonSNSAsync.publish(ArgumentMatchers.any())).then((e) -> {
            if(((PublishRequest)e.getArgument(0)).getMessage().equals("{\"id\": \"1\"}")) {
                throw new RuntimeException("TestException");
            }

            return p2;
        });

        publisher.publish();

        String entityOneStatus = repository.findById(savedEntityOne.getId()).block().getStatus();
        Assert.isTrue(entityOneStatus.equals("PENDING"), "wrong status for entity");

        String entityOneStatusTwo = repository.findById(savedEntityTwo.getId()).block().getStatus();
        Assert.isTrue(entityOneStatusTwo.equals("PENDING"), "wrong status for entity");
    }
}
