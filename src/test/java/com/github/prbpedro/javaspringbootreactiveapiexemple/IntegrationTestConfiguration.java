package com.github.prbpedro.javaspringbootreactiveapiexemple;

import com.github.prbpedro.javaspringbootreactiveapiexemple.config.AwsSnsConfig;
import com.github.prbpedro.javaspringbootreactiveapiexemple.config.AwsSqsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;

import java.net.URI;

public class IntegrationTestConfiguration {

    public static final String DUMB_TOPIC_NAME = "DumbTopic";
    public static final String DUMB_QUEUE_NAME = "DumbQueue";

    private static final SnsAsyncClient SNS_ASYNC_CLIENT =
        SnsAsyncClient
            .builder()
            .credentialsProvider(FakeAwsCredentials::new)
            .endpointOverride(URI.create(System.getenv(AwsSnsConfig.AWS_SNS_ENDPOINT_VARIABLE_NAME)))
            .build();

    private static final SqsAsyncClient SQS_ASYNC_CLIENT =
        SqsAsyncClient
            .builder()
            .credentialsProvider(FakeAwsCredentials::new)
            .endpointOverride(URI.create(System.getenv(AwsSqsConfig.AWS_SQS_ENDPOINT_VARIABLE_NAME)))
            .build();

    static class FakeAwsCredentials implements AwsCredentials {
        @Override
        public String accessKeyId() {
            return "FAKE";
        }

        @Override
        public String secretAccessKey() {
            return "FAKE";
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTestConfiguration.class);

    private static Topic dumbTopic;
    private static String dumbQueueUrl;

    public static Topic getDumbTopic() {
        return dumbTopic;
    }

    public static String getDumbQueueUrl() {
        return dumbQueueUrl;
    }

    public static void configure() {

        try {
            if (SNS_ASYNC_CLIENT.listTopics().get().topics().stream().filter(s -> s.topicArn().contains(DUMB_TOPIC_NAME)).count() < 1) {
                SNS_ASYNC_CLIENT.createTopic(
                    CreateTopicRequest
                        .builder()
                        .name(DUMB_TOPIC_NAME)
                        .build())
                    .get();
            }

            dumbTopic = (Topic) SNS_ASYNC_CLIENT.listTopics().get().topics().stream().filter(s -> s.topicArn().contains(DUMB_TOPIC_NAME)).toArray()[0];
            if (SQS_ASYNC_CLIENT.listQueues().get().queueUrls().stream().filter(s -> s.contains(DUMB_QUEUE_NAME)).count() < 1) {
                SQS_ASYNC_CLIENT.createQueue(
                    CreateQueueRequest
                        .builder()
                        .queueName(DUMB_QUEUE_NAME)
                        .build())
                    .get();
            }

            GetQueueUrlResponse getQueueUrlResponse = SQS_ASYNC_CLIENT.getQueueUrl(GetQueueUrlRequest.builder().queueName(DUMB_QUEUE_NAME).build()).get();

            dumbQueueUrl = getQueueUrlResponse.queueUrl();

            SQS_ASYNC_CLIENT.purgeQueue(PurgeQueueRequest.builder().queueUrl(dumbQueueUrl).build());

            if (SNS_ASYNC_CLIENT.listSubscriptions().get().subscriptions().stream().filter(s -> s.topicArn().contains(DUMB_TOPIC_NAME)).count() < 1) {
                SNS_ASYNC_CLIENT.subscribe(
                    SubscribeRequest
                        .builder()
                        .topicArn(dumbTopic.topicArn())
                        .endpoint(dumbQueueUrl)
                        .protocol("sqs")
                        .build())
                    .get();
            }

        } catch (Exception e) {
            LOGGER.error("Error configuring IntegrationTests infrastructure", e);
            throw new RuntimeException(e);
        }
    }
}
