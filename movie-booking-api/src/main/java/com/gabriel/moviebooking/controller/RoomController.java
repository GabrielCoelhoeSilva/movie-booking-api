package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.room.RoomCreateRequestDTO;
import com.gabriel.moviebooking.dto.room.RoomResponseDTO;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import com.gabriel.moviebooking.service.RoomService;
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
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Salas", description = "Endpoints para gerenciamento de salas de cinema (requer role ADMIN para escrita)")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Cadastrar sala", description = "Cria uma nova sala vinculada a um cinema. Requer role ADMIN.")
    @ApiResponse(responseCode = "201", description = "Sala cadastrada com sucesso",
            content = @Content(schema = @Schema(implementation = RoomResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou cinema não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<RoomResponseDTO> create(@Valid @RequestBody RoomCreateRequestDTO dto) {

        log.info("Tentativa de cadastro de nova sala: '{}' para o Cinema ID: {}", dto.getName(), dto.getCinemaId());

        RoomResponseDTO createdRoom = roomService.create(dto);

        log.info("Sala '{}' cadastrada com sucesso. ID gerado: {}", createdRoom.getName(), createdRoom.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar sala", description = "Atualiza os dados de uma sala. Requer role ADMIN.")
    @ApiResponse(responseCode = "200", description = "Sala atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = RoomResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Sala não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<RoomResponseDTO> update(@PathVariable Long id,
                                                  @Valid @RequestBody RoomCreateRequestDTO dto) {

        log.info("Solicitada atualização para a sala ID: {}. Novos dados - Nome: '{}'", id, dto.getName());

        RoomResponseDTO updatedRoom = roomService.update(id, dto);

        log.info("Sala ID: {} atualizada com sucesso.", id);

        return ResponseEntity.ok(updatedRoom);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sala por ID", description = "Retorna os dados de uma sala específica.")
    @ApiResponse(responseCode = "200", description = "Sala encontrada",
            content = @Content(schema = @Schema(implementation = RoomResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Sala não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<RoomResponseDTO> findById(@PathVariable Long id) {

        log.info("Buscando dados da sala ID: {}", id);

        RoomResponseDTO room = roomService.findById(id);

        return ResponseEntity.ok(room);
    }

    @GetMapping
    @Operation(summary = "Listar salas", description = "Retorna todas as salas cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<RoomResponseDTO>> findAll() {

        log.info("Listando todas as salas cadastradas no sistema");

        List<RoomResponseDTO> rooms = roomService.findAll();

        log.info("Total de salas listadas: {}", rooms.size());

        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar sala", description = "Remove uma sala. Requer role ADMIN.")
    @ApiResponse(responseCode = "204", description = "Sala removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Sala não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        log.info("Solicitada exclusão da sala ID: {}", id);

        roomService.delete(id);

        log.info("Sala ID: {} excluída com sucesso do sistema", id);

        return ResponseEntity.noContent().build();
    }
}