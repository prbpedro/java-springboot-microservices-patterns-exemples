package com.github.prbpedro.javaspringbootreactiveapiexemple.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
public class DumbEntity {

    @Id
    @Column
    private Long id;

    @Column
    private Long value;
}
