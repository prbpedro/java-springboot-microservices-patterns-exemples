package com.github.prbpedro.javaspringbootreactiveapiexemple.services;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityWriteRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class DumbEntityService {

    @Autowired
    private final DumbEntityWriteRepository writeRepository;

    @Autowired
    private final DumbEntityWriteRepository readRepository;

    public Mono<DumbEntityDTO> save(DumbEntityDTO dto) {
        return writeRepository.save(dto.buildEntity()).map(DumbEntity::buildDto);
    }

    public Mono<Void> delete(DumbEntityDTO dto) {
        return writeRepository.delete(dto.buildEntity());
    }

    public Mono<DumbEntityDTO> get(Long id) {
        return readRepository.findById(id)
            .flatMap(dumbEntity -> Mono.just(dumbEntity.buildDto()));
    }

    public Flux<DumbEntityDTO> listAll() {
        return readRepository.findAll()
            .flatMap(dumbEntity -> Mono.just(dumbEntity.buildDto()));
    }
}
