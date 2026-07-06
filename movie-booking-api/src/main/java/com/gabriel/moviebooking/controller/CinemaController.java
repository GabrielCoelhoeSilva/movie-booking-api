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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

        log.info("Tentativa de cadastro de uma nova unidade de cinema: '{}'", dto.getName());

        CinemaResponseDTO created = service.create(dto);

        log.info("Unidade de cinema '{}' cadastrada com sucesso. ID gerado: {}", created.getName(), created.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar cinemas", description = "Retorna todos os cinemas cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<CinemaResponseDTO>> findAll() {

        log.info("Listando todos os cinemas disponíveis no sistema");

        List<CinemaResponseDTO> cinemas = service.findAll();

        log.info("Total de estabelecimentos de cinema encontrados: {}", cinemas.size());

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
        log.info("Buscando dados cadastrais do cinema ID: {}", id);

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
        log.info("Solicitada atualização cadastral para o cinema ID: {}. Novo Nome enviado: '{}'", id, dto.getName());

        CinemaResponseDTO updated = service.update(id, dto);

        log.info("Dados do cinema ID: {} atualizados com sucesso", id);

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
        log.info("Solicitada a exclusão total do cinema ID: {}", id);

        service.delete(id);

        log.info("Cinema ID: {} e todas as suas dependências associadas foram excluídos do sistema", id);

        return ResponseEntity.noContent().build();
    }
}