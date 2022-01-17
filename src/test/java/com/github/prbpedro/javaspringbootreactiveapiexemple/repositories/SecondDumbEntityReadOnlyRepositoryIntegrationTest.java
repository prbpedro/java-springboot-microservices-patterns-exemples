package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.SecondDumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.readonly.SecondDumbEntityReadOnlyRepository;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.SecondDumbEntityWriteRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import reactor.test.StepVerifier;

@SpringBootTest
public class SecondDumbEntityReadOnlyRepositoryIntegrationTest {

    @Autowired
    SecondDumbEntityReadOnlyRepository repository;

    @Autowired
    SecondDumbEntityWriteRepository writeRepository;

    @Test
    public void insertTest() {

        writeRepository.save(SecondDumbEntity.builder().uuid("uuid").value(0L).build());

        StepVerifier
                .create(repository.findByUuid("uuid"))
                .expectNextCount(1)
                .verifyComplete();

    }
}
