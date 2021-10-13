package com.github.prbpedro.javaspringbootreactiveapiexemple.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import com.github.prbpedro.javaspringbootreactiveapiexemple.entities.SecondDumbEntity;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.readonly.SecondDumbEntityReadOnlyRepository;
import com.github.prbpedro.javaspringbootreactiveapiexemple.repositories.write.SecondDumbEntityWriteRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@AllArgsConstructor
public class DumbQueueTransactionalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DumbQueueTransactionalService.class);

    @Autowired
    private final SecondDumbEntityReadOnlyRepository readOnlyRepository;

    @Autowired
    private final SecondDumbEntityWriteRepository writeRepository;

    public Mono processMessage(String messagePayload, Map<String, Object> headers) {
        return Mono
            .just(messagePayload)
            .flatMap(m -> {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> messageMap;
                Map<String, Object> messageBodyMap;
                Map<String, Object> messageAttributesMap;

                try {
                    messageMap = objectMapper.readValue(messagePayload, Map.class);
                    messageBodyMap = objectMapper.readValue((String) messageMap.get("Message"), Map.class);
                    messageAttributesMap = (Map<String, Object>) messageMap.get("MessageAttributes");
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error parsing message", e);
                }

                DumbEntityDTO dto = DumbEntityDTO
                    .builder()
                    .uuid(messageBodyMap.get("uuid").toString())
                    .value(Long.parseLong(messageBodyMap.get("value").toString()))
                    .build();

                return Mono.just(dto.buildSecondDumbEntity());
            })
            .flatMap(e -> readOnlyRepository
                    .findByUuid(e.getUuid())
                    .defaultIfEmpty(new SecondDumbEntity())
                    .flatMap(foundEntity -> {
                        if(foundEntity.getId() != null) {
                            LOGGER.warn("Message already processed");
                            return Mono.just(e);
                        }

                        return Mono
                            .just(e)
                            .flatMap(writeRepository::save);
                    })
            );

    }
}
