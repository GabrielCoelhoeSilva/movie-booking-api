package com.gabriel.moviebooking.controller.docs;

import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieResponseDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Filmes", description = "Endpoints para gerenciamento de filmes (requer role ADMIN para escrita)")
public interface MovieControllerDocs {

    @Operation(summary = "Cadastrar filme", description = "Cria um novo filme. Requer role ADMIN.")
    @ApiResponse(responseCode = "201", description = "Filme cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = MovieResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<MovieResponseDTO> create(@Valid @RequestBody MovieRequestDTO dto);

    @Operation(summary = "Buscar filme por ID", description = "Retorna os dados de um filme específico.")
    @ApiResponse(responseCode = "200", description = "Filme encontrado",
            content = @Content(schema = @Schema(implementation = MovieResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Filme não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<MovieResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Listar filmes", description = "Retorna todos os filmes cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    ResponseEntity<List<MovieResponseDTO>> findAll();

    @Operation(summary = "Atualizar filme", description = "Atualiza os dados de um filme. Requer role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Filme atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = MovieResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Filme não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<MovieResponseDTO> update(@PathVariable Long id, @Valid @RequestBody MovieUpdateDTO dto);

    @Operation(summary = "Deletar filme", description = "Remove um filme. Requer role ADMIN.")
    @ApiResponse(responseCode = "204", description = "Filme removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<Void> delete(@PathVariable Long id);
}