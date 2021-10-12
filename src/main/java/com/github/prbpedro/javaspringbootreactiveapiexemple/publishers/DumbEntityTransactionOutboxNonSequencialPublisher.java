package com.github.prbpedro.javaspringbootreactiveapiexemple.publishers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
@AllArgsConstructor
public class DumbEntityTransactionOutboxNonSequencialPublisher {

    public static final String DUMB_TOPIC_ARN = "DUMB_TOPIC_ARN";
    public static final int PUBLISHER_SCHEDULER_FIXED_DELAY_MILLIS = 300000;

    @Autowired
    private final DumbEntityTransactionOutboxWriteRepository repository;

    @Autowired
    private final SnsAsyncClient snsAsyncClient;

    @Scheduled(fixedDelay = PUBLISHER_SCHEDULER_FIXED_DELAY_MILLIS)
    public void publish() {
        selectAndUpdateStatus()
            .blockLast();
    }

    @Transactional
    public Flux<DumbEntityTransactionOutbox> selectAndUpdateStatus() {
        return repository
            .findPendingEntitiesLimited()
            .flatMap(this::updateEntity)
            .flatMap(this::sendEvent);
    }

    public Mono<DumbEntityTransactionOutbox> updateEntity(DumbEntityTransactionOutbox e) {
        e.setStatus("PROCESSED");
        return repository.save(e);
    }

    public Mono<DumbEntityTransactionOutbox> sendEvent(DumbEntityTransactionOutbox e) {
        return Mono
            .fromFuture(() ->
                snsAsyncClient
                    .publish(
                        PublishRequest
                            .builder()
                            .topicArn(System.getenv(DUMB_TOPIC_ARN))
                            .message(e.getMessageBody())
                            .messageAttributes(e.buildMessageAttributesMap())
                            .build()))
            .map(t -> {
                if (t.sdkHttpResponse().statusCode() != HttpStatusCode.OK) {
                    throw new RuntimeException("Error publishing message to SNS");
                }

                return e;
            });
    }
}
