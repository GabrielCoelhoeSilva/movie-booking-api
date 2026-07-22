package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.controller.docs.SessionControllerDocs;
import com.gabriel.moviebooking.dto.session.SessionCreateRequestDTO;
import com.gabriel.moviebooking.dto.session.SessionResponseDTO;
import com.gabriel.moviebooking.service.SessionService;
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
public class SessionController implements SessionControllerDocs {

    private final SessionService service;

    @Override
    @PostMapping
    public ResponseEntity<SessionResponseDTO> create(@Valid @RequestBody SessionCreateRequestDTO dto) {
        log.info("Recebida requisição para criar sessão. Filme ID: {}, Sala ID: {}, Horário: {} ", dto.getMovieId(), dto.getRoomId(), dto.getStartTime());

        SessionResponseDTO response = service.create(dto);

        log.info("Sessão criada com sucesso. ID: {} (Assentos gerados automaticamente", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDTO> findById(@PathVariable Long id) {
        log.info("Buscando detalhes da sessão com ID: {}", id);

        SessionResponseDTO response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<SessionResponseDTO>> findAll() {
        log.info("Listando todas as sessões de cinema cadastradas");

        List<SessionResponseDTO> response = service.findAll();

        log.info("Total de sessões encontradas: {}", response.size());

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Recebida requisição para deletar sessão com ID: {} e remover seus assentos vinculados", id);

        service.delete(id);

        log.info("Sessão com ID: {} deletada com sucesso do sistema", id);

        return ResponseEntity.noContent().build();
    }
}