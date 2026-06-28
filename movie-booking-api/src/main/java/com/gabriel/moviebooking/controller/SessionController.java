package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.session.SessionCreateRequestDTO;
import com.gabriel.moviebooking.dto.session.SessionResponseDTO;
import com.gabriel.moviebooking.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;

    @PostMapping
    public ResponseEntity<SessionResponseDTO> create(
            @Valid @RequestBody SessionCreateRequestDTO dto) {

        SessionResponseDTO response = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDTO> findById(
            @PathVariable Long id) {

        SessionResponseDTO response = service.findById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SessionResponseDTO>> findAll() {

        List<SessionResponseDTO> response = service.findAll();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}