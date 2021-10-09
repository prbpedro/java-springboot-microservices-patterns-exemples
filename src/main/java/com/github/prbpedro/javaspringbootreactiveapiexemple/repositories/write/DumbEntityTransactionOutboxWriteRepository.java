package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface DumbEntityTransactionOutboxWriteRepository extends R2dbcRepository<DumbEntityTransactionOutbox, Long> {
}
