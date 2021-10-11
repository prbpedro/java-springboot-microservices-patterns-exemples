package com.github.prbpedro.javaspringbootreactiveapiexemple.publishers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.JavaSpringbootReactiveApiExempleApplication;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.concurrent.ExecutionException;

@SpringBootTest
public class DumbEntityTransactionOutboxNonSequencialPublisherIntegrationTests {

    @Autowired
    private SnsAsyncClient snsAsyncClient;

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Autowired
    private DumbEntityTransactionOutboxNonSequencialPublisher publisher;

    @Autowired
    private DumbEntityTransactionOutboxWriteRepository repository;

    private Topic dumbTopic;
    private String queueUrl;

    @BeforeEach
    public void configure() throws ExecutionException, InterruptedException {
        repository.deleteAll().block();

        if (snsAsyncClient.listTopics().get().topics().stream().filter(s -> s.topicArn().contains("DumbTopic")).count() < 1) {
            snsAsyncClient.createTopic(
                CreateTopicRequest
                    .builder()
                    .name("DumbTopic")
                    .build())
                .get();
        }

        dumbTopic = (Topic) snsAsyncClient.listTopics().get().topics().stream().filter(s -> s.topicArn().contains("DumbTopic")).toArray()[0];
        if (sqsAsyncClient.listQueues().get().queueUrls().stream().filter(s -> s.contains("DumbTopicTestQueue")).count() < 1) {
            sqsAsyncClient.createQueue(
                CreateQueueRequest
                    .builder()
                    .queueName("DumbTopicTestQueue")
                    .build())
                .get();
        }

        GetQueueUrlResponse getQueueUrlResponse = sqsAsyncClient.getQueueUrl(GetQueueUrlRequest.builder().queueName("DumbTopicTestQueue").build()).get();

        queueUrl = getQueueUrlResponse.queueUrl();

        sqsAsyncClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(queueUrl).build());

        if (snsAsyncClient.listSubscriptions().get().subscriptions().stream().filter(s -> s.topicArn().contains("DumbTopic")).count() < 1) {
            snsAsyncClient.subscribe(
                SubscribeRequest
                    .builder()
                    .topicArn(dumbTopic.topicArn())
                    .endpoint(queueUrl)
                    .protocol("sqs")
                    .build())
                .get();
        }
    }

    @Test
    public void publishPendingMessagesTest() throws ExecutionException, InterruptedException {
        repository.save(
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

        repository.save(
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
        Thread.sleep(300);
        GetQueueAttributesResponse getQueueAttributesResponse = sqsAsyncClient.getQueueAttributes(
            GetQueueAttributesRequest
                .builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                .build())
            .get();
        String messageNumberString = getQueueAttributesResponse.attributesAsStrings().get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES.toString());
        Assert.isTrue(Integer.parseInt(messageNumberString) == 2, "wrong number of messages sent");
    }
}
