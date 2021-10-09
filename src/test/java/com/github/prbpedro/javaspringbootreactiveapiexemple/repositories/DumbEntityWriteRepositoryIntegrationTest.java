package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityWriteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.util.Assert;
import reactor.test.StepVerifier;

@SpringBootTest
public class DumbEntityWriteRepositoryIntegrationTest {

    @Autowired
    DumbEntityWriteRepository repository;

    @Autowired
    DumbEntityTransactionOutboxWriteRepository dumbEntityTransactionOutboxWriteRepository;

    @BeforeTestMethod
    public void beforeAll() {
        dumbEntityTransactionOutboxWriteRepository.deleteAll().subscribe();
        repository.deleteAll().subscribe();
    }

    @Test
    public void insertTest() {

        StepVerifier
            .create(repository.save(DumbEntity.builder().value(1L).build()))
            .assertNext(dumbEntity -> {
                Assert.notNull(dumbEntity, "Returned entity should not be null");
                Assert.notNull(dumbEntity.getId(), "Returned entity should not be null");
            })
            .verifyComplete();

    }
}
