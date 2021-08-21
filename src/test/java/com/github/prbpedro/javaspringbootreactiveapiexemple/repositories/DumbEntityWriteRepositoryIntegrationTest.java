package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.readonly.DumbEntityReadOnlyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import reactor.test.StepVerifier;

import javax.naming.OperationNotSupportedException;

@SpringBootTest
public class DumbEntityWriteRepositoryIntegrationTest {

    @Autowired
    DumbEntityReadOnlyRepository repository;

    @Test
    public void insertTest() {

        StepVerifier
            .create(repository.save(new DumbEntity()))
            .assertNext(dumbEntity -> {
                Assert.notNull(dumbEntity, "Returned entity should not be null");
                Assert.notNull(dumbEntity.getId(), "Returned entity should not be null");
            });

    }
}
