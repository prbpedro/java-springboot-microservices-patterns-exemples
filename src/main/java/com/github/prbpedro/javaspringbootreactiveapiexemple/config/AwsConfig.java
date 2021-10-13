package com.github.prbpedro.javaspringbootreactiveapiexemple.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;

import javax.annotation.PostConstruct;
import java.net.URI;

@Configuration
public class AwsConfig {

    public static final String DUMB_TOPIC_NAME = "DumbTopic";
    public static final String DUMB_QUEUE_NAME = "DumbQueue";

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsConfig.class);

    private static Topic dumbTopic;

    public static String getDumbTopicArn() {
        return dumbTopic.topicArn();
    }

    @Bean
    public static SnsAsyncClient amazonSnsAsyncClient() {
        return SnsAsyncClient
            .builder()
            .endpointOverride(URI.create(EnvironmentVariables.AWS_SNS_ENDPOINT.getValue()))
            .build();
    }

    @Bean
    public static SqsAsyncClient amazonSqsAsyncClient() {
        return SqsAsyncClient
            .builder()
            .endpointOverride(URI.create(EnvironmentVariables.AWS_SQS_ENDPOINT.getValue()))
            .build();
    }

    @PostConstruct
    public void configure() {

        try {
            SnsAsyncClient snsAsyncClient = amazonSnsAsyncClient();
            SqsAsyncClient sqsAsyncClient = amazonSqsAsyncClient();

            if (snsAsyncClient.listTopics().get().topics().stream().filter(s -> s.topicArn().contains(DUMB_TOPIC_NAME)).count() < 1) {
                snsAsyncClient.createTopic(
                    CreateTopicRequest
                        .builder()
                        .name(DUMB_TOPIC_NAME)
                        .build())
                    .get();
            }

            dumbTopic = (Topic) snsAsyncClient.listTopics().get().topics().stream().filter(s -> s.topicArn().contains(DUMB_TOPIC_NAME)).toArray()[0];
            if (sqsAsyncClient.listQueues().get().queueUrls().stream().filter(s -> s.contains(DUMB_QUEUE_NAME)).count() < 1) {
                sqsAsyncClient.createQueue(
                    CreateQueueRequest
                        .builder()
                        .queueName(DUMB_QUEUE_NAME)
                        .build())
                    .get();
            }

            GetQueueUrlResponse getQueueUrlResponse = sqsAsyncClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(DUMB_QUEUE_NAME).build()).get();

            String dumbQueueUrl = getQueueUrlResponse.queueUrl();

            sqsAsyncClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(dumbQueueUrl).build());

            if (snsAsyncClient.listSubscriptions().get().subscriptions().stream().filter(s -> s.topicArn().contains(DUMB_TOPIC_NAME)).count() < 1) {
                snsAsyncClient.subscribe(
                    SubscribeRequest
                        .builder()
                        .topicArn(dumbTopic.topicArn())
                        .endpoint(dumbQueueUrl)
                        .protocol("sqs")
                        .build())
                    .get();
            }

            LOGGER.info("Infrastructure configured");
        } catch (Exception e) {
            LOGGER.error("Error configuring infrastructure", e);
            throw new RuntimeException(e);
        }
    }
}
