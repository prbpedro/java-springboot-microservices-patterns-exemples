package com.github.prbpedro.javaspringbootreactiveapiexemple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
public class AwsSqsConfig {

    @Bean
    public SqsAsyncClient amazonSqsAsyncClient() {
        return SqsAsyncClient
            .builder()
            .endpointOverride(URI.create(System.getenv("AWS_SNS_ENDPOINT")))
            .build();
    }
}
