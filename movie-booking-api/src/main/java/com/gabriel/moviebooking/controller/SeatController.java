package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.controller.docs.SeatControllerDocs;
import com.gabriel.moviebooking.dto.seat.SeatResponseDTO;
import com.gabriel.moviebooking.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController implements SeatControllerDocs {

    private final SeatService service;

    @Override
    @GetMapping
    public ResponseEntity<List<SeatResponseDTO>> findBySessionId(@RequestParam Long sessionId) {
        log.info("Buscando mapa de assentos para a sessão com ID: {}", sessionId);

        List<SeatResponseDTO> response = service.findBySessionId(sessionId);

        log.info("Mapa de assentos retornado com sucesso para a sessão ID: {}. Total de assentos: {}",
                sessionId, response.size());

        return ResponseEntity.ok(response);
    }
}