package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieResponseDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import com.gabriel.moviebooking.service.MovieService;
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
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Tag(name = "Filmes", description = "Endpoints para gerenciamento de filmes (requer role ADMIN para escrita)")
public class MovieController {

    private final MovieService service;

    @PostMapping
    @Operation(summary = "Cadastrar filme", description = "Cria um novo filme. Requer role ADMIN.")
    @ApiResponse(responseCode = "201", description = "Filme cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = MovieResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<MovieResponseDTO> create(
            @Valid @RequestBody MovieRequestDTO dto) {

        MovieResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar filme por ID", description = "Retorna os dados de um filme específico.")
    @ApiResponse(responseCode = "200", description = "Filme encontrado",
            content = @Content(schema = @Schema(implementation = MovieResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Filme não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<MovieResponseDTO> findById(
            @PathVariable Long id) {

        MovieResponseDTO response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar filmes", description = "Retorna todos os filmes cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<MovieResponseDTO>> findAll() {

        List<MovieResponseDTO> response = service.findAll();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar filme", description = "Atualiza os dados de um filme. Requer role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Filme atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = MovieResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Filme não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<MovieResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MovieUpdateDTO dto) {

        MovieResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar filme", description = "Remove um filme. Requer role ADMIN.")
    @ApiResponse(responseCode = "204", description = "Filme removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
