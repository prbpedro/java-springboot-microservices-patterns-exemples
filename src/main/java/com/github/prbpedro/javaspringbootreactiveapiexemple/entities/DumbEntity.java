package com.github.prbpedro.javaspringbootreactiveapiexemple.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class DumbEntity {

    @Id
    private Long id;
}
