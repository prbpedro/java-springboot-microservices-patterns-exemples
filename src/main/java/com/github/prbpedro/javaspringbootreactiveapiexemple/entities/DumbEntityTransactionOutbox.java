package com.github.prbpedro.javaspringbootreactiveapiexemple.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DumbEntityTransactionOutbox {

    @Id
    @Column
    @EqualsAndHashCode.Include
    private Long id;

    @Column
    private Long dumbEntityId;

    @Column
    private String generatedUuid;

    @Column
    private String operation;

    @Column
    private String messageBody;

    @Column
    private String messageAttributes;

    @Column
    private LocalDateTime createdAt;

    @Column
    private String status;
}
