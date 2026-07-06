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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

        log.info("Recebida requisição para cadastrar novo filme: '{}'", dto.getTitle());

        MovieResponseDTO response = service.create(dto);

        log.info("Filme cadastrado com sucesso. ID: {}", response.getId());

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

        log.info("Buscando filme com ID {}", id);

        MovieResponseDTO response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar filmes", description = "Retorna todos os filmes cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<MovieResponseDTO>> findAll() {

        log.info("Listando todos os filmes cadastrados");

        List<MovieResponseDTO> response = service.findAll();

        log.info("Total de filmes encontrados: {}", response.size());

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

        log.info("Recebida requisição para atualizar o filme com ID: {}", id);

        MovieResponseDTO response = service.update(id, dto);

        log.info("Filme com ID: {} atualizado com sucesso", id);

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

        log.info("Recebida requisição para deletar o filme com ID: {}", id);

        service.delete(id);

        log.info("Filme com ID: {} deletado com sucesso", id);

        return ResponseEntity.noContent().build();
    }
}
