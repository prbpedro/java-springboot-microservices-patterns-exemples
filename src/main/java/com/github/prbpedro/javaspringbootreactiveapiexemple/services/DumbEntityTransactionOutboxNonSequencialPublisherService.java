package com.github.prbpedro.javaspringbootreactiveapiexemple.services;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.PublishRequest;
import com.github.prbpedro.javaspringbootreactiveapiexemple.config.AwsConfig;
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

@Service
@AllArgsConstructor
public class DumbEntityTransactionOutboxNonSequencialPublisherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DumbEntityTransactionOutboxNonSequencialPublisherService.class);

    @Autowired
    public final DumbEntityTransactionOutboxWriteRepository repository;

    @Autowired
    public AmazonSNSAsync amazonSNSAsync;

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

        return Mono
            .just(e)
            .flatMap(repository::save)
            .doOnError(t -> LOGGER.error("Error updating DumbEntityTransactionOutbox status to PROCESSED", t));
    }

    private Mono<DumbEntityTransactionOutbox> sendEvent(DumbEntityTransactionOutbox e) {
        PublishRequest publishRequest = new PublishRequest();
        publishRequest.setTopicArn(AwsConfig.getDumbTopicArn());
        publishRequest.setMessage(e.getMessageBody());
        publishRequest.setMessageAttributes(e.buildMessageAttributesMap());

        return Mono
            .just(publishRequest)
            .map(it -> amazonSNSAsync.publish(it))
            .map(it -> {
                if (it.getSdkHttpMetadata().getHttpStatusCode() != HttpStatusCode.OK) {
                    throw new RuntimeException("SNS Publish Message in Topic Response Status Code NOT 200");
                }

                return e;
            })
            .doOnError(t ->
                LOGGER.error("Error publishing message to SNS Topic. PublishRequest: " + publishRequest.toString(), t));
    }
}
