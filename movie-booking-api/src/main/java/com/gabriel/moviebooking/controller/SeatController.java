package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.seat.SeatResponseDTO;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import com.gabriel.moviebooking.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
@Tag(name = "Assentos", description = "Endpoints para consulta de assentos de uma sessão")
public class SeatController {

    private final SeatService service;

    @GetMapping
    @Operation(
            summary = "Listar assentos por sessão",
            description = "Retorna todos os assentos de uma sessão específica, com status de disponibilidade. Use esse endpoint para montar o mapa de assentos antes de fazer uma reserva."
    )
    @ApiResponse(responseCode = "200", description = "Lista de assentos retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<List<SeatResponseDTO>> findBySessionId(
            @Parameter(description = "ID da sessão", required = true)
            @RequestParam Long sessionId) {

        log.info("Buscando mapa de assentos para a sessão com ID: {}", sessionId);

        List<SeatResponseDTO> response = service.findBySessionId(sessionId);

        log.info("Mapa de assentos retornado com sucesso para a sessão ID: {}. Total de assentos: {}",
                sessionId, response.size());

        return ResponseEntity.ok(response);
    }
}