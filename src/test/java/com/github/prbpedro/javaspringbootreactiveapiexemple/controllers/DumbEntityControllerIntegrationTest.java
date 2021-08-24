package com.github.prbpedro.javaspringbootreactiveapiexemple.controllers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityWriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class DumbEntityControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    DumbEntityWriteRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository.deleteAll().block();
        repository.save(DumbEntity.builder().build()).block();
    }

    @Test
    public void shouldListAllDumbEntitiesTest() {
        webTestClient
            .get()
            .uri("/dumb/list")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(DumbEntityDTO.class);
    }

    @Test
    public void shouldGetDumbEntityTest() {
        DumbEntity d = repository.save(DumbEntity.builder().build()).block();
        webTestClient
            .get()
            .uri("/dumb/{id}", d.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(DumbEntityDTO.class);
    }

    @Test
    public void shouldPutDumbEntityTest() {
        webTestClient
            .put()
            .uri("/dumb")
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(DumbEntityDTO.builder().build()), DumbEntityDTO.class)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(DumbEntityDTO.class);
    }

    @Test
    public void shouldDeleteDumbEntityTest() {
        DumbEntity d = repository.save(DumbEntity.builder().build()).block();
        webTestClient
            .delete()
            .uri("/dumb/{id}", d.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk();
    }

}
