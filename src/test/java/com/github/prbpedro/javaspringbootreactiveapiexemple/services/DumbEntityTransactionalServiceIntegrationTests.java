package com.github.prbpedro.javaspringbootreactiveapiexemple.services;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityWriteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.util.Assert;
import reactor.test.StepVerifier;

@SpringBootTest
public class DumbEntityTransactionalServiceIntegrationTests {

    @Autowired
    DumbEntityWriteRepository repository;

    @Autowired
    DumbEntityTransactionOutboxWriteRepository dumbEntityTransactionOutboxWriteRepository;

    @Autowired
    DumbEntityTransactionalService transactionalService;

    @Autowired
    DumbEntityService service;


    @BeforeTestMethod
    public void beforeAll() {
        dumbEntityTransactionOutboxWriteRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    public void saveTest() {
        StepVerifier
            .create(transactionalService.save(DumbEntityDTO.builder().value(1L).build()))
            .assertNext(dto -> {
                Assert.notNull(dto, "Returned entity should not be null");
                Assert.notNull(dto.getDumbEntity(), "Returned entity should not be null");
                Assert.notNull(dto.getDumbEntityTransactionOutbox(), "Returned entity should not be null");
            })
            .verifyComplete();
    }

    @Test
    public void getTest() {
        StepVerifier
            .create(
                transactionalService
                    .save(DumbEntityDTO.builder().value(1L).build())
                    .flatMap(dumbEntityDTO -> service.get(dumbEntityDTO.getId()))
            )
            .assertNext(dto -> {
                Assert.notNull(dto, "Returned entity should not be null");
                Assert.notNull(dto.getId(), "Returned entity should not be null");
            })
            .verifyComplete();
    }

    @Test
    public void deleteTest() {
        StepVerifier
            .create(
                transactionalService
                    .save(DumbEntityDTO.builder().value(1L).build())
                    .flatMap(dumbEntityDTO -> dumbEntityTransactionOutboxWriteRepository
                            .deleteById(dumbEntityDTO.getDumbEntityTransactionOutbox().getId())
                            .map(e -> dumbEntityDTO)
                    )
                    .flatMap(dumbEntityDTO -> transactionalService.delete(dumbEntityDTO))
                    .flatMap(dumbEntityDto -> service.get(1L))
            )
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    public void listAllTest() {
        StepVerifier
            .create(
                transactionalService
                    .save(DumbEntityDTO.builder().value(1L).build())
                    .map(dumbEntityDTO -> service.listAll())
            )
            .expectNextCount(1)
            .verifyComplete();
    }
}
