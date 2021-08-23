package com.github.prbpedro.javaspringbootreactiveapiexemple.entities;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@EqualsAndHashCode(exclude = {"value"})
public class DumbEntity {

    @Id
    @Column
    private Long id;

    @Column
    private Long value;

    public DumbEntityDTO buildDto() {
        return DumbEntityDTO.builder().id(id).value(value).build();
    }
}
