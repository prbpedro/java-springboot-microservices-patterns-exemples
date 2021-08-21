package com.github.prbpedro.javaspringbootreactiveapiexemple.dto;

import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(exclude = {"value"})
public class DumbEntityDto {
    private Long id;
    private Long value;

    public DumbEntity getEntity() {
        return DumbEntity.builder().id(id).value(value).build();
    }
}
