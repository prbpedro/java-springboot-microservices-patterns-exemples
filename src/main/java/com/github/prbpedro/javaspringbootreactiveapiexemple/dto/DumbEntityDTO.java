package com.github.prbpedro.javaspringbootreactiveapiexemple.dto;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DumbEntityDTO {

    private Long id;

    private Long value;

    public DumbEntity buildEntity() {
        return DumbEntity.builder().id(id).value(value).build();
    }
}
