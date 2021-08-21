package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.readonly.DumbEntityReadOnlyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import reactor.test.StepVerifier;

@SpringBootTest
public class DumbEntityReadOnlyRepositoryIntegrationTest {

    @Autowired
    DumbEntityReadOnlyRepository repository;

    @Test
    public void insertTest() {

        StepVerifier
            .create(repository.save(DumbEntity.builder().value(1L).build()))
            .expectError(DataAccessResourceFailureException.class)
            .verify();

    }
}
