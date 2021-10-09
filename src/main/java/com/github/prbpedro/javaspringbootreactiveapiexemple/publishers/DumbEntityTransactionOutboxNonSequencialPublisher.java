package com.github.prbpedro.javaspringbootreactiveapiexemple.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class DumbEntityTransactionOutboxNonSequencialPublisher {

    @Autowired
    private final DumbEntityTransactionOutboxWriteRepository repository;

    @Autowired
    private final SnsAsyncClient snsAsyncClient;

    @Scheduled(fixedDelay = 100000)
    public void publish() {
        selectAndUpdateStatus()
            .subscribe();
    }

    @Transactional
    public Flux<DumbEntityTransactionOutbox> selectAndUpdateStatus() {
        return repository
            .findPendingEntitiesLimited()
            .flatMap(this::updateEntity)
            .flatMap(this::sendEvents);
    }
    public Mono<DumbEntityTransactionOutbox> updateEntity(DumbEntityTransactionOutbox e) {
        e.setStatus("PROCESSED");
        return repository.save(e);
    }

    public Mono<DumbEntityTransactionOutbox> sendEvents(DumbEntityTransactionOutbox e) {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> attributes;
        try {
            attributes = mapper.readValue(e.getMessageAttributes(), new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException("Error reading message attributes");
        }

        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        attributes.forEach((k, v) -> messageAttributes.put(k, MessageAttributeValue.builder().stringValue(v).dataType("String").build()));

        try {
            snsAsyncClient
                .publish(
                    PublishRequest
                        .builder()
                        .topicArn(System.getenv("DUMB_TOPIC_ARN"))
                        .message(e.getMessageBody())
                        .messageAttributes(messageAttributes)
                        .build())
                .get();
        } catch (Throwable t) {
            throw new RuntimeException("Error sending message", t);
        }
        return Mono.just(e);
    }
}
