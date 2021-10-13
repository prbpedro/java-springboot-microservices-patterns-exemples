package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface DumbEntityTransactionOutboxWriteRepository extends R2dbcRepository<DumbEntityTransactionOutbox, Long> {

    @Query("SELECT * FROM dumb_entity_transaction_outbox where status = 'PENDING' LIMIT 10 FOR UPDATE; ")
    Flux<DumbEntityTransactionOutbox> findPendingEntitiesLimited();
}
