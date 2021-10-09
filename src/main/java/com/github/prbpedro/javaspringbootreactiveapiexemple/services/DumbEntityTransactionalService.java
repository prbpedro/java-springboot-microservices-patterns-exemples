package com.github.prbpedro.javaspringbootreactiveapiexemple.services;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityTransactionOutboxWriteRepository;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.DumbEntityWriteRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class DumbEntityTransactionalService {

    @Autowired
    private final DumbEntityWriteRepository writeRepository;

    @Autowired
    private final DumbEntityTransactionOutboxWriteRepository dumbEntityTransactionOutboxWriteRepository;

    public Mono<DumbEntityDTO> save(DumbEntityDTO dto) {
        dto.setUuid(UUID.randomUUID().toString());

        if (dto.getId() != null) {
            return Mono
                .just(dto)
                .flatMap(dumbEntityDTO ->
                    writeRepository
                        .save(dto.buildEntity())
                        .map(e ->
                            {
                                dumbEntityDTO.setUuid(dto.getUuid());
                                dumbEntityDTO.setId(e.getId());
                                dumbEntityDTO.setValue(e.getValue());
                                dumbEntityDTO.setDumbEntity(e);
                                dumbEntityDTO.setOperation("UPDATE");
                                return dumbEntityDTO;
                            }
                        )
                )
                .flatMap(dumbEntityDTO ->
                    dumbEntityTransactionOutboxWriteRepository
                        .save(dumbEntityDTO.buildEntityOutbox())
                        .map(e ->
                            {
                                dumbEntityDTO.setDumbEntityTransactionOutbox(e);
                                return dumbEntityDTO;
                            }
                        )
                );
        }


        return writeRepository
            .save(dto.buildEntity())
            .map(e ->
                {
                    dto.setId(e.getId());
                    dto.setDumbEntity(e);
                    dto.setOperation("INSERT");
                    return dto;
                }
            )
            .flatMap(dumbEntityDTO ->
                dumbEntityTransactionOutboxWriteRepository
                    .save(dumbEntityDTO.buildEntityOutbox())
                    .map(e ->
                        {
                            dumbEntityDTO.setDumbEntityTransactionOutbox(e);
                            return dumbEntityDTO;
                        }
                    )
            );
    }


    public Mono<Void> delete(DumbEntityDTO dto) {
        dto.setUuid(UUID.randomUUID().toString());
        dto.setOperation("DELETE");
        dto.setDumbEntity(dto.buildEntity());
        return Mono
            .just(dto)
            .flatMap(s ->
                dumbEntityTransactionOutboxWriteRepository
                    .save(dto.buildEntityOutbox())
                    .map(e -> s)
            )
            .flatMap(s -> writeRepository
                .findById(dto.getId())
                .defaultIfEmpty(new DumbEntity())
                .map(e -> {
                    if (e.getId() == null) {
                        throw new RuntimeException("Entity not found");
                    }
                    return dto;
                }))
            .flatMap(d ->
                writeRepository
                    .delete(d.getDumbEntity())
            );
    }
}
