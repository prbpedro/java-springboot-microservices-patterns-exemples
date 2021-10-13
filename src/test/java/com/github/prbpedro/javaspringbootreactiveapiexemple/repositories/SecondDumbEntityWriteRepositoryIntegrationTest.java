package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.SecondDumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.SecondDumbEntityWriteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.util.Assert;
import reactor.test.StepVerifier;

@SpringBootTest
public class SecondDumbEntityWriteRepositoryIntegrationTest {

    @Autowired
    SecondDumbEntityWriteRepository repository;

    @BeforeTestMethod
    public void before() {
        repository.deleteAll().subscribe();
    }

    @Test
    public void insertTest() {

        StepVerifier
            .create(repository.save(SecondDumbEntity.builder().uuid("uuid1").value(1L).build()))
            .assertNext(secondDumbEntity -> {
                Assert.notNull(secondDumbEntity, "Returned entity should not be null");
                Assert.notNull(secondDumbEntity.getId(), "Returned entity should not be null");
            })
            .verifyComplete();
    }

    @Test
    public void uuidUniqueIndexTest() {

        StepVerifier
            .create(repository
                .save(SecondDumbEntity.builder().uuid("uuid").value(1L).build())
                .flatMap(e -> repository.save(SecondDumbEntity.builder().uuid("uuid").value(1L).build()))
            )
            .expectError(DataIntegrityViolationException.class)
            .verify();
    }
}
