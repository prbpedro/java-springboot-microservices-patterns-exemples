package com.github.prbpedro.javaspringbootreactiveapiexemple.consumers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.SecondDumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.readonly.SecondDumbEntityReadOnlyRepository;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.SecondDumbEntityWriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.HashMap;

@SpringBootTest
public class DumbQueueConsumerIntegrationTests {

    @Autowired
    private DumbQueueConsumer dumbQueueConsumer;

    @Autowired
    private SecondDumbEntityWriteRepository repository;


    @Autowired
    private SecondDumbEntityReadOnlyRepository readOnlyRepository;

    @BeforeEach
    public void beforeEach() {
        repository.deleteAll().block();
    }

    @Test
    public void consumeMessageTest() {

        dumbQueueConsumer.consumeMessage(
            "{\"Message\": \"{\\\"uuid\\\":\\\"uuid\\\", \\\"value\\\":\\\"1\\\"}\", \"MessageAttributes\": {\"uuid\":\"1\", \"value\":\"1\"}}",
            new HashMap<>());

        dumbQueueConsumer.consumeMessage(
            "{\"Message\": \"{\\\"uuid\\\":\\\"uuid\\\", \\\"value\\\":\\\"1\\\"}\", \"MessageAttributes\": {\"uuid\":\"1\", \"value\":\"1\"}}",
            new HashMap<>());

        SecondDumbEntity es = readOnlyRepository.findByUuid("uuid").block();
        Assert.isTrue(es != null, "entity not found");
    }
}
