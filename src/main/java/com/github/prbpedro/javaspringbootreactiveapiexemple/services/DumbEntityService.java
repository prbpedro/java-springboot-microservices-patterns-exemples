package com.github.prbpedro.javaspringbootreactiveapiexemple.services;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityWriteRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.rmi.UnexpectedException;
import java.util.UUID;

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
