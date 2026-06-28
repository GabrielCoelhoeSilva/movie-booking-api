package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaResponseDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
import com.gabriel.moviebooking.service.CinemaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cinemas")
public class CinemaController {

    private final CinemaService service;

    public CinemaController(CinemaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CinemaResponseDTO> create(
            @RequestBody @Valid CinemaCreateDTO dto
    ) {
        CinemaResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CinemaResponseDTO>> findAll() {
        List<CinemaResponseDTO> cinemas = service.findAll();
        return ResponseEntity.ok(cinemas);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CinemaResponseDTO> findById(
            @PathVariable Long id
    ) {
        CinemaResponseDTO cinema = service.findById(id);
        return ResponseEntity.ok(cinema);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CinemaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid CinemaUpdateDTO dto
    ) {
        CinemaResponseDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}