package com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.SecondDumbEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface SecondDumbEntityWriteRepository extends R2dbcRepository<SecondDumbEntity, Long> {
}
