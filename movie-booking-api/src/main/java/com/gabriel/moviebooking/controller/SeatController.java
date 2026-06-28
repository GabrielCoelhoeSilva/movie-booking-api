package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.seat.SeatResponseDTO;
import com.gabriel.moviebooking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService service;

    @GetMapping
    public ResponseEntity<List<SeatResponseDTO>> findBySessionId(
            @RequestParam Long sessionId) {

        List<SeatResponseDTO> response = service.findBySessionId(sessionId);

        return ResponseEntity.ok(response);
    }
}