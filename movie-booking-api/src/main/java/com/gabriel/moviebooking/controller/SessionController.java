package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.session.SessionCreateRequestDTO;
import com.gabriel.moviebooking.dto.session.SessionResponseDTO;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import com.gabriel.moviebooking.service.SessionService;
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
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessões", description = "Endpoints para gerenciamento de sessões de cinema")
public class SessionController {

    private final SessionService service;

    @PostMapping
    @Operation(summary = "Criar sessão", description = "Cria uma nova sessão vinculando filme e sala. Gera os assentos automaticamente. Requer role ADMIN.")
    @ApiResponse(responseCode = "201", description = "Sessão criada com sucesso",
            content = @Content(schema = @Schema(implementation = SessionResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Conflito de horário ou dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Filme ou sala não encontrados",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<SessionResponseDTO> create(
            @Valid @RequestBody SessionCreateRequestDTO dto) {

        log.info("Recebida requisição para criar sessão. Filme ID: {}, Sala ID: {}, Horário: {} ", dto.getMovieId(), dto.getRoomId(),dto.getStartTime());

        SessionResponseDTO response = service.create(dto);

        log.info("Sessão criada com sucesso. ID: {} (Assentos gerados automaticamente", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sessão por ID", description = "Retorna os dados de uma sessão específica.")
    @ApiResponse(responseCode = "200", description = "Sessão encontrada",
            content = @Content(schema = @Schema(implementation = SessionResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<SessionResponseDTO> findById(
            @PathVariable Long id) {

        log.info("Buscando detalhes da sessão com ID: {}", id);

        SessionResponseDTO response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar sessões", description = "Retorna todas as sessões cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<SessionResponseDTO>> findAll() {

        log.info("Listando todas as sessões de cinema cadastradas");

        List<SessionResponseDTO> response = service.findAll();

        log.info("Total de sessões encontradas: {}", response.size());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar sessão", description = "Remove uma sessão e todos os seus assentos. Requer role ADMIN.")
    @ApiResponse(responseCode = "204", description = "Sessão removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        log.info("Recebida requisição para deletar sessão com ID: {} e remover seus assentos vinculados", id);

        service.delete(id);

        log.info("Sessão com ID: {} deletada com sucesso do sistema", id);

        return ResponseEntity.noContent().build();
    }
}