package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.controller.docs.MovieControllerDocs;
import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieResponseDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;
import com.gabriel.moviebooking.service.MovieService;
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
public class MovieController implements MovieControllerDocs {

    private final MovieService service;

    @Override
    @PostMapping
    public ResponseEntity<MovieResponseDTO> create(@Valid @RequestBody MovieRequestDTO dto) {
        log.info("Recebida requisição para cadastrar novo filme: '{}'", dto.getTitle());

        MovieResponseDTO response = service.create(dto);

        log.info("Filme cadastrado com sucesso. ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> findById(@PathVariable Long id) {
        log.info("Buscando filme com ID {}", id);

        MovieResponseDTO response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> findAll() {
        log.info("Listando todos os filmes cadastrados");

        List<MovieResponseDTO> response = service.findAll();

        log.info("Total de filmes encontrados: {}", response.size());

        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> update(@PathVariable Long id, @Valid @RequestBody MovieUpdateDTO dto) {
        log.info("Recebida requisição para atualizar o filme com ID: {}", id);

        MovieResponseDTO response = service.update(id, dto);

        log.info("Filme com ID: {} atualizado com sucesso", id);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Recebida requisição para deletar o filme com ID: {}", id);

        service.delete(id);

        log.info("Filme com ID: {} deletado com sucesso", id);

        return ResponseEntity.noContent().build();
    }
}