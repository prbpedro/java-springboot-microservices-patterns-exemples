package com.github.prbpedro.javaspringbootreactiveapiexemple.services;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityWriteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class DumbEntityService {

    @Autowired
    private final DumbEntityWriteRepository readRepository;

    public Mono<DumbEntityDTO> get(Long id) {
        return readRepository.findById(id)
            .flatMap(dumbEntity -> Mono.just(dumbEntity.buildDto()));
    }

    public Flux<DumbEntityDTO> listAll() {
        return readRepository.findAll()
            .flatMap(dumbEntity -> Mono.just(dumbEntity.buildDto()));
    }
}
