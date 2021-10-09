package com.github.prbpedro.javaspringbootreactiveapiexemple.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sns.SnsAsyncClient;

import java.net.URI;

@Configuration
public class AwsSnsConfig {

    @Bean
    public SnsAsyncClient amazonSnsAsyncClient() {
        return SnsAsyncClient
            .builder()
            .endpointOverride(URI.create(System.getenv("AWS_SNS_ENDPOINT")))
            .build();
    }
}
