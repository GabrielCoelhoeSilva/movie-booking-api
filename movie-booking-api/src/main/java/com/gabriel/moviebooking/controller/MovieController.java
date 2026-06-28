package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieResponseDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;
import com.gabriel.moviebooking.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

    @PostMapping
    public ResponseEntity<MovieResponseDTO> create(
            @Valid @RequestBody MovieRequestDTO dto) {

        MovieResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> findById(
            @PathVariable Long id) {

        MovieResponseDTO response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> findAll() {

        List<MovieResponseDTO> response = service.findAll();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MovieUpdateDTO dto) {

        MovieResponseDTO response = service.update(id, dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
