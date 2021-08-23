package com.github.prbpedro.javaspringbootreactiveapiexemple.controllers;

import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.DumbEntityDTO;
import com.github.prbpedro.javaspringbootreactiveapiexemple.dto.MessageResponseDTO;
import com.github.prbpedro.javaspringbootreactiveapiexemple.services.DumbEntityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/dumb")
public class DumbEntityController {

    public static final String ENTITY_FOUND = "Entity found";
    public static final String ENTITY_PERSISTED = "Entity persisted";
    public static final String ENTITY_DELETED = "Entity deleted";
    public static final String ENTITY_NOT_FOUND = "Entity not found";
    public static final String UNEXPECTED_ERROR = "Unexpected error";
    public static final String BAD_REQUEST = "Bad Request";

    @Autowired
    private final DumbEntityService service;

    @ApiOperation(value = "Dumb GET")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = ENTITY_FOUND, response = DumbEntityDTO.class),
        @ApiResponse(code = 500, message = UNEXPECTED_ERROR, response = MessageResponseDTO.class),
        @ApiResponse(code = 400, message = BAD_REQUEST),
        @ApiResponse(code = 404, message = ENTITY_NOT_FOUND, response = MessageResponseDTO.class)})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> get(
        @RequestParam Long id
    ) {
        return
            service
                .get(id)
                .defaultIfEmpty(DumbEntityDTO.builder().build())
                .map(dumbEntityDTO -> {
                    if (dumbEntityDTO.getId() != null) {
                        return ResponseEntity.ok().body(dumbEntityDTO);
                    }
                    return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(MessageResponseDTO.builder().message(ENTITY_NOT_FOUND).build());
                })
                .onErrorResume(throwable ->
                    Mono.just(
                        ResponseEntity.internalServerError().body(
                            MessageResponseDTO.builder().message(UNEXPECTED_ERROR).build())));
    }

    @ApiOperation(value = "Dumb LIST")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = ENTITY_FOUND, response = DumbEntityDTO[].class),
        @ApiResponse(code = 500, message = UNEXPECTED_ERROR, response = MessageResponseDTO.class)})
    @RequestMapping(value="/list",  method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> list() {
        return
            service
                .listAll()
                .collectList()
                .flatMap(list -> {
                    if (list.size() <= 0) {
                        return Mono.just(
                            ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(MessageResponseDTO.builder().message(ENTITY_NOT_FOUND).build()));
                    }
                    return Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(list));
                })
                .onErrorResume(throwable ->
                    Mono.just(
                        ResponseEntity.internalServerError().body(
                            MessageResponseDTO.builder().message(UNEXPECTED_ERROR).build())));
    }

    @ApiOperation(value = "Dumb PUT")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = ENTITY_PERSISTED, response = DumbEntityDTO.class),
        @ApiResponse(code = 400, message = BAD_REQUEST),
        @ApiResponse(code = 500, message = UNEXPECTED_ERROR, response = MessageResponseDTO.class)})
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> save(
        @RequestBody
        @Validated
            DumbEntityDTO dto
    ) {
        return
            service
                .save(dto)
                .map((rdto) -> ResponseEntity.ok().body(rdto));
    }

    @ApiOperation(value = "Dumb DELETE")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = ENTITY_DELETED, response = MessageResponseDTO.class),
        @ApiResponse(code = 400, message = BAD_REQUEST),
        @ApiResponse(code = 500, message = UNEXPECTED_ERROR, response = MessageResponseDTO.class)})
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<MessageResponseDTO>> delete(
        @RequestBody
        @Validated
            DumbEntityDTO dto
    ) {

        if (dto.getId() == null) {
            return Mono.just(
                ResponseEntity
                    .badRequest()
                    .body(MessageResponseDTO.builder().message(BAD_REQUEST).build()));
        }

        return
            service
                .delete(dto)
                .map((voidObj) ->
                    ResponseEntity
                        .ok()
                        .body(MessageResponseDTO.builder().message(ENTITY_DELETED).build()))
                .onErrorResume(throwable ->
                    Mono.just(
                        ResponseEntity.internalServerError().body(
                            MessageResponseDTO.builder().message(UNEXPECTED_ERROR).build())));
    }
}
