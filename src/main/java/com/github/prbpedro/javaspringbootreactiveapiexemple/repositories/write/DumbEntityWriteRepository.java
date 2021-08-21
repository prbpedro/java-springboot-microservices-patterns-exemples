package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface DumbEntityWriteRepository   extends R2dbcRepository<DumbEntity, Long> {
}
