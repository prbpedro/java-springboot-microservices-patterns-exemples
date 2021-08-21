package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.readonly;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface DumbEntityReadOnlyRepository  extends R2dbcRepository<DumbEntity, Long> {
}
