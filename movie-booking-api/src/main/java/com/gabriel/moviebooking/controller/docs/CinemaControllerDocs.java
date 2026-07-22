package com.gabriel.moviebooking.controller.docs;

import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaResponseDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
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

@Tag(name = "Cinemas", description = "Endpoints para gerenciamento de cinemas (requer role ADMIN para escrita)")
public interface CinemaControllerDocs {

    @Operation(summary = "Cadastrar cinema", description = "Cria um novo cinema. Requer role ADMIN.")
    @ApiResponse(responseCode = "201", description = "Cinema cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = CinemaResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<CinemaResponseDTO> create(@RequestBody @Valid CinemaCreateDTO dto);

    @Operation(summary = "Listar cinemas", description = "Retorna todos os cinemas cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    ResponseEntity<List<CinemaResponseDTO>> findAll();

    @Operation(summary = "Buscar cinema por ID", description = "Retorna os dados de um cinema específico.")
    @ApiResponse(responseCode = "200", description = "Cinema encontrado",
            content = @Content(schema = @Schema(implementation = CinemaResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cinema não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<CinemaResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Atualizar cinema", description = "Atualiza os dados de um cinema. Requer role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Cinema atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = CinemaResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cinema não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<CinemaResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CinemaUpdateDTO dto);

    @Operation(summary = "Deletar cinema", description = "Remove um cinema. Requer role ADMIN.")
    @ApiResponse(responseCode = "204", description = "Cinema removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Cinema não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<Void> delete(@PathVariable Long id);
}