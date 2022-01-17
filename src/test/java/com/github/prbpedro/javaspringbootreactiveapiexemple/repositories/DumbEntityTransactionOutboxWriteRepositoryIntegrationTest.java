package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
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
public class DumbEntityTransactionOutboxWriteRepositoryIntegrationTest {

    @Autowired
    DumbEntityTransactionOutboxWriteRepository repository;

    @Autowired
    DumbEntityWriteRepository dumbEntityWriteRepository;

    @BeforeTestMethod
    public void beforeAll() {
        repository.deleteAll().subscribe();
        dumbEntityWriteRepository.deleteAll().subscribe();
    }

    @Test
    public void insertTest() {

        StepVerifier
            .create(
                dumbEntityWriteRepository
                    .save(DumbEntity.builder().value(1L).build())
                    .flatMap(e -> {
                            DumbEntityDTO dto = e.buildDto();
                            dto.setOperation("operation");
                            dto.setUuid("uuid");
                            dto.setDumbEntity(e);
                            return repository
                                .save(dto.buildEntityOutbox())
                                .flatMap(eo -> repository.findById(eo.getId()));
                        }
                    )
            )
            .assertNext(dumbEntityOutbox -> {
                Assert.notNull(dumbEntityOutbox, "Returned entity should not be null");
                Assert.notNull(dumbEntityOutbox.getId(), "Returned entity should not be null");
            })
            .verifyComplete();

    }
}
