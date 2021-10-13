package com.github.prbpedro.javaspringbootreactiveapiexemple.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.Topic;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

@Configuration
public class AwsConfig {

    public static final String DUMB_TOPIC_NAME = "DumbTopic";
    public static final String DUMB_QUEUE_NAME = "DumbQueue";

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsConfig.class);

    private static Topic dumbTopic;

    public static String getDumbTopicArn() {
        return dumbTopic.getTopicArn();
    }

    @Bean
    @Primary
    public static AmazonSNSAsync amazonSNSAsync() {
        return AmazonSNSAsyncClientBuilder
            .standard()
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(
                    EnvironmentVariables.AWS_SQS_ENDPOINT.getValue(),
                    EnvironmentVariables.AWS_REGION.getValue()))
            .build();
    }

    @Bean
    @Primary
    public static AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder
            .standard()
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(
                    EnvironmentVariables.AWS_SQS_ENDPOINT.getValue(),
                    EnvironmentVariables.AWS_REGION.getValue()))
            .build();
    }

    @PostConstruct
    public void configure() {

        try {
            AmazonSNSAsync amazonSNSAsync = amazonSNSAsync();
            AmazonSQSAsync amazonSQSAsync = amazonSQSAsync();

            if (amazonSNSAsync.listTopics().getTopics().stream().filter(s -> s.getTopicArn().contains(DUMB_TOPIC_NAME)).count() < 1) {
                CreateTopicRequest createTopicRequest = new CreateTopicRequest();
                createTopicRequest.setName(DUMB_TOPIC_NAME);
                amazonSNSAsync.createTopic(createTopicRequest);
            }

            dumbTopic = (Topic) amazonSNSAsync.listTopics().getTopics().stream().filter(s -> s.getTopicArn().contains(DUMB_TOPIC_NAME)).toArray()[0];
            if (amazonSQSAsync.listQueues().getQueueUrls().stream().filter(s -> s.contains(DUMB_QUEUE_NAME)).count() < 1) {
                CreateQueueRequest createQueueRequest = new CreateQueueRequest();
                createQueueRequest.setQueueName(DUMB_QUEUE_NAME);
                amazonSQSAsync.createQueue(createQueueRequest);
            }

            GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest();
            getQueueUrlRequest.setQueueName(DUMB_QUEUE_NAME);
            GetQueueUrlResult getQueueUrlResponse = amazonSQSAsync.getQueueUrl(getQueueUrlRequest);

            String dumbQueueUrl = getQueueUrlResponse.getQueueUrl();

            PurgeQueueRequest purgeQueueRequest = new PurgeQueueRequest();
            purgeQueueRequest.setQueueUrl(dumbQueueUrl);
            amazonSQSAsync.purgeQueue(purgeQueueRequest);

            if (amazonSNSAsync.listSubscriptions().getSubscriptions().stream().filter(s -> s.getTopicArn().contains(DUMB_TOPIC_NAME)).count() < 1) {
                SubscribeRequest subscribeRequest = new SubscribeRequest();
                subscribeRequest.setTopicArn(dumbTopic.getTopicArn());
                subscribeRequest.setEndpoint(dumbQueueUrl);
                subscribeRequest.setProtocol("sqs");
                amazonSNSAsync.subscribe(subscribeRequest);
            }

            LOGGER.info("Infrastructure configured");
        } catch (Exception e) {
            LOGGER.error("Error configuring infrastructure", e);
            throw new RuntimeException(e);
        }
    }
}
