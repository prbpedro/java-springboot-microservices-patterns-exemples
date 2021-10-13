package com.github.prbpedro.javaspringbootreactiveapiexemple.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.DumbEntityTransactionOutbox;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.SecondDumbEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DumbEntityDTO {

    private Long id;

    private Long value;

    @JsonIgnore
    private DumbEntity dumbEntity;

    @JsonIgnore
    private String uuid;

    @JsonIgnore
    private String operation;

    @JsonIgnore
    private DumbEntityTransactionOutbox dumbEntityTransactionOutbox;

    public DumbEntity buildEntity() {
        return DumbEntity.builder().id(id).value(value).build();
    }

    public SecondDumbEntity buildSecondDumbEntity() {
        return SecondDumbEntity.builder().id(id).value(value).uuid(uuid).build();
    }

    public DumbEntityTransactionOutbox buildEntityOutbox() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> messageBodyMap = mapper.convertValue(dumbEntity, new TypeReference<Map<String, Object>>() {});
        messageBodyMap.put("operation", operation);
        messageBodyMap.put("uuid", uuid);

        Map<String, Object> messageAttributesMap = new HashMap<>();
        messageAttributesMap.put("operation", operation);
        messageAttributesMap.put("uuid", uuid);

        try {
            return DumbEntityTransactionOutbox
                    .builder()
                    .operation(operation)
                    .generatedUuid(uuid)
                    .messageBody(mapper.writeValueAsString(messageBodyMap))
                    .messageAttributes(mapper.writeValueAsString(messageAttributesMap))
                    .dumbEntityId(dumbEntity.getId())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
