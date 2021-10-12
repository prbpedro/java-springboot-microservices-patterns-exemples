package com.github.prbpedro.javaspringbootreactiveapiexemple.services;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
@AllArgsConstructor
public class DumbEntityTransactionOutboxNonSequencialPublisherService {

    public static final String DUMB_TOPIC_ARN = "DUMB_TOPIC_ARN";

    private static final Logger LOGGER = LoggerFactory.getLogger(DumbEntityTransactionOutboxNonSequencialPublisherService.class);

    @Autowired
    public final DumbEntityTransactionOutboxWriteRepository repository;

    @Autowired
    public SnsAsyncClient snsAsyncClient;

    @Transactional
    public Flux<DumbEntityTransactionOutbox> selectAndUpdateStatus() {
        return repository
            .findPendingEntitiesLimited()
            .flatMap(this::updateEntity)
            .flatMap(this::sendEvent)
            .map(d -> {
                LOGGER.info("Message published to SNS Topic");
                return d;
            });
    }

    private Mono<DumbEntityTransactionOutbox> updateEntity(DumbEntityTransactionOutbox e) {
        e.setStatus("PROCESSED");
        return repository
            .save(e)
            .doOnError(t -> LOGGER.error("Error updating DumbEntityTransactionOutbox status to PROCESSED", t));
    }

    private Mono<DumbEntityTransactionOutbox> sendEvent(DumbEntityTransactionOutbox e) {
        PublishRequest publishRequest = PublishRequest
            .builder()
            .topicArn(System.getenv(DUMB_TOPIC_ARN))
            .message(e.getMessageBody())
            .messageAttributes(e.buildMessageAttributesMap())
            .build();

        return Mono
            .fromFuture(() -> snsAsyncClient.publish(publishRequest))
            .map(t -> {
                if (t.sdkHttpResponse().statusCode() != HttpStatusCode.OK) {
                    throw new RuntimeException("SNS Publish Message in Topic Response Status Code NOT 200");
                }

                return e;
            })
            .doOnError(t ->
                LOGGER.error("Error publishing message to SNS Topic. PublishRequest: " + publishRequest.toString(), t));
    }
}
