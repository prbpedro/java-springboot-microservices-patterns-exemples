package com.github.prbpedro.javaspringbootreactiveapiexemple.publishers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class DumbEntityTransactionOutboxNonSequencialPublisherIntegrationTests {

    @Autowired
    private DumbEntityTransactionOutboxNonSequencialPublisher publisher;

    @Autowired
    private DumbEntityTransactionOutboxWriteRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository.deleteAll().block();
    }

    @Test
    public void publishPendingMessagesTest() {
        DumbEntityTransactionOutbox savedEntityOne = repository.save(
            DumbEntityTransactionOutbox
                .builder()
                .dumbEntityId(1L)
                .generatedUuid("generatedUuid")
                .operation("operation")
                .messageBody("{\"id\":\"1\"}")
                .messageAttributes("{\"uuid\":\"1\", \"operation\":\"1\"}")
                .status("PENDING")
                .build())
            .block();

        DumbEntityTransactionOutbox savedEntityTwo = repository.save(
            DumbEntityTransactionOutbox
                .builder()
                .dumbEntityId(1L)
                .generatedUuid("generatedUuid")
                .operation("operation")
                .messageBody("{\"id\":\"1\"}")
                .messageAttributes("{\"uuid\":\"1\", \"operation\":\"1\"}")
                .status("PENDING")
                .build())
            .block();

        publisher.publish();

        String entityOneStatus = repository.findById(savedEntityOne.getId()).block().getStatus();
        Assert.isTrue(entityOneStatus.equals("PROCESSED"), "wrong status for entity");

        String entityTwoStatus = repository.findById(savedEntityTwo.getId()).block().getStatus();
        Assert.isTrue(entityTwoStatus.equals("PROCESSED"), "wrong status for entity");
    }
}
