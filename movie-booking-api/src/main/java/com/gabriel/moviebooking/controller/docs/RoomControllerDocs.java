package com.gabriel.moviebooking.controller.docs;

import com.gabriel.moviebooking.dto.room.RoomCreateRequestDTO;
import com.gabriel.moviebooking.dto.room.RoomResponseDTO;
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

@Tag(name = "Salas", description = "Endpoints para gerenciamento de salas de cinema (requer role ADMIN para escrita)")
public interface RoomControllerDocs {

    @Operation(summary = "Cadastrar sala", description = "Cria uma nova sala vinculada a um cinema. Requer role ADMIN.")
    @ApiResponse(responseCode = "201", description = "Sala cadastrada com sucesso",
            content = @Content(schema = @Schema(implementation = RoomResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou cinema não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<RoomResponseDTO> create(@Valid @RequestBody RoomCreateRequestDTO dto);

    @Operation(summary = "Atualizar sala", description = "Atualiza os dados de uma sala. Requer role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Sala atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = RoomResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Sala não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<RoomResponseDTO> update(@PathVariable Long id, @Valid @RequestBody RoomCreateRequestDTO dto);

    @Operation(summary = "Buscar sala por ID", description = "Retorna os dados de uma sala específica.")
    @ApiResponse(responseCode = "200", description = "Sala encontrada",
            content = @Content(schema = @Schema(implementation = RoomResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Sala não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<RoomResponseDTO> findById(@PathVariable Long id);

    @Operation(summary = "Listar salas", description = "Retorna todas as salas cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    ResponseEntity<List<RoomResponseDTO>> findAll();

    @Operation(summary = "Deletar sala", description = "Remove uma sala. Requer role ADMIN.")
    @ApiResponse(responseCode = "204", description = "Sala removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Sala não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<Void> delete(@PathVariable Long id);
}