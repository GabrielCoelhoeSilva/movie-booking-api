package com.gabriel.moviebooking.controller.docs;

import com.gabriel.moviebooking.dto.seat.SeatResponseDTO;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Assentos", description = "Endpoints para consulta de assentos de uma sessão")
public interface SeatControllerDocs {

    @Operation(
            summary = "Listar assentos por sessão",
            description = "Retorna todos os assentos de uma sessão específica, com status de disponibilidade. Use esse endpoint para montar o mapa de assentos antes de fazer uma reserva."
    )
    @ApiResponse(responseCode = "200", description = "Lista de assentos retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<List<SeatResponseDTO>> findBySessionId(
            @Parameter(description = "ID da sessão", required = true)
            @RequestParam Long sessionId);
}