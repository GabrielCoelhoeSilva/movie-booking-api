package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.controller.docs.CinemaControllerDocs;
import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaResponseDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
import com.gabriel.moviebooking.service.CinemaService;
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
public class CinemaController implements CinemaControllerDocs {

    private final CinemaService service;

    @Override
    @PostMapping
    public ResponseEntity<CinemaResponseDTO> create(@RequestBody @Valid CinemaCreateDTO dto) {
        log.info("Tentativa de cadastro de uma nova unidade de cinema: '{}'", dto.getName());

        CinemaResponseDTO created = service.create(dto);

        log.info("Unidade de cinema '{}' cadastrada com sucesso. ID gerado: {}", created.getName(), created.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<CinemaResponseDTO>> findAll() {
        log.info("Listando todos os cinemas disponíveis no sistema");

        List<CinemaResponseDTO> cinemas = service.findAll();

        log.info("Total de estabelecimentos de cinema encontrados: {}", cinemas.size());

        return ResponseEntity.ok(cinemas);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CinemaResponseDTO> findById(@PathVariable Long id) {
        log.info("Buscando dados cadastrais do cinema ID: {}", id);

        CinemaResponseDTO cinema = service.findById(id);

        return ResponseEntity.ok(cinema);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<CinemaResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CinemaUpdateDTO dto) {
        log.info("Solicitada atualização cadastral para o cinema ID: {}. Novo Nome enviado: '{}'", id, dto.getName());

        CinemaResponseDTO updated = service.update(id, dto);

        log.info("Dados do cinema ID: {} atualizados com sucesso", id);

        return ResponseEntity.ok(updated);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Solicitada a exclusão total do cinema ID: {}", id);

        service.delete(id);

        log.info("Cinema ID: {} e todas as suas dependências associadas foram excluídos do sistema", id);

        return ResponseEntity.noContent().build();
    }
}