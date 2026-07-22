package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.controller.docs.BookingControllerDocs;
import com.gabriel.moviebooking.dto.booking.BookingCreateRequestDTO;
import com.gabriel.moviebooking.dto.booking.BookingResponseDTO;
import com.gabriel.moviebooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController implements BookingControllerDocs {

    private final BookingService service;

    @Override
    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(@Valid @RequestBody BookingCreateRequestDTO dto,
                                                     Authentication authentication) {
        log.info("Tentativa de criar reserva para a Sessão ID: {} com os Assentos IDs: {}",
                dto.getSessionId(), dto.getSeatIds());

        BookingResponseDTO response = service.create(dto, authentication);

        log.info("Reserva criada com sucesso. ID: {} | Status: PENDING (Expira em 10 minutos)", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponseDTO> confirm(@PathVariable Long id, Authentication authentication) {
        log.info("Solicitada a confirmação do pagamento/reserva para o ID: {}", id);

        BookingResponseDTO response = service.confirm(id, authentication);

        log.info("Reserva ID: {} CONFIRMADA com sucesso no sistema", id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancel(@PathVariable Long id, Authentication authentication) {
        log.info("Solicitado o cancelamento da reserva ID: {}", id);

        BookingResponseDTO response = service.cancel(id, authentication);

        log.info("Reserva ID: {} foi CANCELADA com sucesso e os assentos correspondentes foram liberados", id);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> findById(@PathVariable Long id) {
        log.info("Buscando detalhes da reserva ID: {}", id);

        return ResponseEntity.ok(service.findById(id));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<List<BookingResponseDTO>> findMyBookings(Authentication authentication) {
        log.info("Listando o histórico de reservas do usuário atual");

        List<BookingResponseDTO> response = service.findMyBookings(authentication);

        log.info("Total de reservas encontradas para este usuário: {}", response.size());

        return ResponseEntity.ok(response);
    }
}