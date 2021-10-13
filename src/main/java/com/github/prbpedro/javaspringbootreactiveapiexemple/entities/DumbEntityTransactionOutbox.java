package com.github.prbpedro.javaspringbootreactiveapiexemple.entities;

import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
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

    public Map<String, MessageAttributeValue> buildMessageAttributesMap() {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> attributes;
        try {
            attributes = mapper.readValue(messageAttributes != null ? messageAttributes : "", new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException("Error reading message attributes");
        }

        Map<String, MessageAttributeValue> messageAttributesMap = new HashMap<>();
        attributes.forEach((k, v) -> {
            MessageAttributeValue m = new MessageAttributeValue();
            m.setStringValue(v);
            m.setDataType("String");
            messageAttributesMap.put(k, m);

        });

        return messageAttributesMap;
    }
}
