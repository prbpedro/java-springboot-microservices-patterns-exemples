package com.github.prbpedro.javaspringbootreactiveapiexemple.consumers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.config.AwsConfig;
import com.github.prbpedro.javaspringbootreactiveapiexemple.services.DumbQueueTransactionalService;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component@AllArgsConstructor
public class DumbQueueConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DumbQueueConsumer.class);

    @Autowired
    private final DumbQueueTransactionalService dumbQueueTransactionalService;

    @SqsListener(value = AwsConfig.DUMB_QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void consumeMessage(
        String messageBody,
        @Headers
        Map<String, Object> headers) {
        dumbQueueTransactionalService
            .processMessage(messageBody, headers)
            .block();
    }
}
