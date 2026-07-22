package com.gabriel.moviebooking.controller.docs;

import com.gabriel.moviebooking.dto.session.SessionCreateRequestDTO;
import com.gabriel.moviebooking.dto.session.SessionResponseDTO;
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

@Tag(name = "Sessões", description = "Endpoints para gerenciamento de sessões de cinema")
public interface SessionControllerDocs {

    @Operation(summary = "Criar sessão", description = "Cria uma nova sessão vinculando filme e sala. Gera os assentos automaticamente. Requer role ADMIN.")
    @ApiResponse(responseCode = "201", description = "Sessão criada com sucesso",
            content = @Content(schema = @Schema(implementation = SessionResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Conflito de horário ou dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Filme ou sala não encontrados",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<SessionResponseDTO> create(@Valid @RequestBody SessionCreateRequestDTO dto);

    @Operation(summary = "Buscar sessão por ID", description = "Retorna os dados de uma sessão específica.")
    @ApiResponse(responseCode = "200", description = "Sessão encontrada",
            content = @Content(schema = @Schema(implementation = SessionResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<SessionResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Listar sessões", description = "Retorna todas as sessões cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    ResponseEntity<List<SessionResponseDTO>> findAll();

    @Operation(summary = "Deletar sessão", description = "Remove uma sessão e todos os seus assentos. Requer role ADMIN.")
    @ApiResponse(responseCode = "204", description = "Sessão removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<Void> delete(@PathVariable Long id);
}