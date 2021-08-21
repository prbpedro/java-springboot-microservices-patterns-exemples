package com.github.prbpedro.javaspringbootreactiveapiexemple.services;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDto;
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

    public Mono<DumbEntityDto> save(DumbEntityDto dto) {
        return writeRepository.save(dto.getEntity()).flatMap(dumbEntity -> Mono.just(dumbEntity.getDto()));
    }

    public Mono<Void> delete(DumbEntityDto dto) {
        return writeRepository.delete(dto.getEntity());
    }

    public Mono<DumbEntityDto> get(Long id) {
        return readRepository.findById(id)
            .flatMap(dumbEntity -> Mono.just(dumbEntity.getDto()));
    }

    public Flux<DumbEntityDto> listAll() {
        return readRepository.findAll()
            .flatMap(dumbEntity -> Mono.just(dumbEntity.getDto()));
    }
}
