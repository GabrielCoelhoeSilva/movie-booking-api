package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.booking.BookingCreateRequestDTO;
import com.gabriel.moviebooking.dto.booking.BookingResponseDTO;
import com.gabriel.moviebooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(
            @Valid @RequestBody BookingCreateRequestDTO dto,
            Authentication authentication) {

        BookingResponseDTO response = service.create(dto, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponseDTO> confirm(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(service.confirm(id, authentication));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancel(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(service.cancel(id, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingResponseDTO>> findMyBookings(Authentication authentication) {
        return ResponseEntity.ok(service.findMyBookings(authentication));
    }
}