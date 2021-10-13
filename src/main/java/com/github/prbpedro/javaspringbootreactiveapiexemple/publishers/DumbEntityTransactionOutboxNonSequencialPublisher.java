package com.github.prbpedro.javaspringbootreactiveapiexemple.publishers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.services.DumbEntityTransactionOutboxNonSequencialPublisherService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class DumbEntityTransactionOutboxNonSequencialPublisher {

    public static final int PUBLISHER_SCHEDULER_FIXED_DELAY_MILLIS = 10000;

    private static final Logger LOGGER = LoggerFactory.getLogger(DumbEntityTransactionOutboxNonSequencialPublisher.class);

    @Autowired
    public final DumbEntityTransactionOutboxNonSequencialPublisherService service;

    @Scheduled(fixedDelay = PUBLISHER_SCHEDULER_FIXED_DELAY_MILLIS)
    public void publish() {
        service
            .selectAndUpdateStatus()
            .collectList()
            .onErrorResume(t -> {
                LOGGER.error("Error executing OUTBOX Publisher", t);
                return Mono.empty();
            })
            .block();
    }
}
