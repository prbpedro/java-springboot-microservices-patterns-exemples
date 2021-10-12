package com.github.prbpedro.javaspringbootreactiveapiexemple.consumers;

import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class DumbQueueConsumer {

//    @SqsListener(value = "my-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void consumeMessage() {

    }
}
