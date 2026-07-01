package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaResponseDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import com.gabriel.moviebooking.service.CinemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cinemas")
@RequiredArgsConstructor
@Tag(name = "Cinemas", description = "Endpoints para gerenciamento de cinemas (requer role ADMIN para escrita)")
public class CinemaController {

    private final CinemaService service;


    @PostMapping
    @Operation(summary = "Cadastrar cinema", description = "Cria um novo cinema. Requer role ADMIN.")
    @ApiResponse(responseCode = "201", description = "Cinema cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = CinemaResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<CinemaResponseDTO> create(
            @RequestBody @Valid CinemaCreateDTO dto
    ) {
        CinemaResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar cinemas", description = "Retorna todos os cinemas cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<CinemaResponseDTO>> findAll() {
        List<CinemaResponseDTO> cinemas = service.findAll();
        return ResponseEntity.ok(cinemas);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar cinema por ID", description = "Retorna os dados de um cinema específico.")
    @ApiResponse(responseCode = "200", description = "Cinema encontrado",
            content = @Content(schema = @Schema(implementation = CinemaResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cinema não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<CinemaResponseDTO> findById(
            @PathVariable Long id
    ) {
        CinemaResponseDTO cinema = service.findById(id);
        return ResponseEntity.ok(cinema);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cinema", description = "Atualiza os dados de um cinema. Requer role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Cinema atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = CinemaResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cinema não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<CinemaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid CinemaUpdateDTO dto
    ) {
        CinemaResponseDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cinema", description = "Remove um cinema. Requer role ADMIN.")
    @ApiResponse(responseCode = "204", description = "Cinema removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Cinema não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}