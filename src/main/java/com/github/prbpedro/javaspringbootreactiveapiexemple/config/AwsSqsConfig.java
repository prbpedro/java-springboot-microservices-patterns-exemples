package com.github.prbpedro.javaspringbootreactiveapiexemple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
public class AwsSqsConfig {

    public static final String AWS_SQS_ENDPOINT_VARIABLE_NAME = "AWS_SQS_ENDPOINT";

    @Bean
    public SqsAsyncClient amazonSqsAsyncClient() {
        return SqsAsyncClient
            .builder()
            .endpointOverride(URI.create(System.getenv(AWS_SQS_ENDPOINT_VARIABLE_NAME)))
            .build();
    }
}
