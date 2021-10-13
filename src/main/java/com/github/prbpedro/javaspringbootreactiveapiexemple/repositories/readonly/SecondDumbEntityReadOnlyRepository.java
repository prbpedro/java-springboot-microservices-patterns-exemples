package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.readonly;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.SecondDumbEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SecondDumbEntityReadOnlyRepository extends R2dbcRepository<SecondDumbEntity, Long> {

    @Query("SELECT * FROM second_dumb_entity where uuid = :uuid; ")
    Mono<SecondDumbEntity> findByUuid(String uuid);
}
