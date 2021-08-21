package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
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

    @BeforeTestMethod
    public void beforeAll() {
        repository.deleteAll();
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
