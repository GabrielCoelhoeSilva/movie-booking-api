package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.booking.BookingCreateRequestDTO;
import com.gabriel.moviebooking.dto.booking.BookingResponseDTO;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import com.gabriel.moviebooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reservas", description = "Endpoints para gerenciamento de reservas de ingressos")
public class BookingController {

    private final BookingService service;

    @PostMapping
    @Operation(
            summary = "Criar reserva",
            description = "Cria uma nova reserva com status PENDING. A reserva expira em 10 minutos se não for confirmada. Requer autenticação."
    )
    @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso",
            content = @Content(schema = @Schema(implementation = BookingResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Assento indisponível ou não pertence à sessão",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Sessão ou assento não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<BookingResponseDTO> create(
            @Valid @RequestBody BookingCreateRequestDTO dto,
            Authentication authentication) {

        log.info("Tentativa de criar reserva para a Sessão ID: {} com os Assentos IDs: {}",
                dto.getSessionId(), dto.getSeatIds());

        BookingResponseDTO response = service.create(dto, authentication);

        log.info("Reserva criada com sucesso. ID: {} | Status: PENDING (Expira em 10 minutos)", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/confirm")
    @Operation(
            summary = "Confirmar reserva",
            description = "Confirma uma reserva com status PENDING. Só o dono da reserva ou um ADMIN pode confirmar. A reserva não pode estar expirada."
    )
    @ApiResponse(responseCode = "200", description = "Reserva confirmada com sucesso",
            content = @Content(schema = @Schema(implementation = BookingResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Reserva expirada ou já confirmada/cancelada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Reserva não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<BookingResponseDTO> confirm(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("Solicitada a confirmação do pagamento/reserva para o ID: {}", id);

        BookingResponseDTO response = service.confirm(id, authentication);

        log.info("Reserva ID: {} CONFIRMADA com sucesso no sistema", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @Operation(
            summary = "Cancelar reserva",
            description = "Cancela uma reserva PENDING ou CONFIRMED, liberando os assentos. Só o dono ou um ADMIN pode cancelar."
    )
    @ApiResponse(responseCode = "200", description = "Reserva cancelada com sucesso",
            content = @Content(schema = @Schema(implementation = BookingResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Reserva já cancelada ou expirada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Reserva não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<BookingResponseDTO> cancel(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("Solicitado o cancelamento da reserva ID: {}", id);

        BookingResponseDTO response = service.cancel(id, authentication);

        log.info("Reserva ID: {} foi CANCELADA com sucesso e os assentos correspondentes foram liberados", id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar reserva por ID",
            description = "Retorna os dados de uma reserva específica. Requer autenticação."
    )
    @ApiResponse(responseCode = "200", description = "Reserva encontrada",
            content = @Content(schema = @Schema(implementation = BookingResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Reserva não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<BookingResponseDTO> findById(@PathVariable Long id) {

        log.info("Buscando detalhes da reserva ID: {}", id);

        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Minhas reservas",
            description = "Retorna todas as reservas do usuário autenticado."
    )
    @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<List<BookingResponseDTO>> findMyBookings(Authentication authentication) {
        log.info("Listando o histórico de reservas do usuário atual");

        List<BookingResponseDTO> response = service.findMyBookings(authentication);

        log.info("Total de reservas encontradas para este usuário: {}", response.size());

        return ResponseEntity.ok(response);
    }
}