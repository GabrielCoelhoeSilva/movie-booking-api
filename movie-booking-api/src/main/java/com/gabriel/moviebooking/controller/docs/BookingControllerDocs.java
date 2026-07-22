package com.gabriel.moviebooking.controller.docs;

import com.gabriel.moviebooking.dto.booking.BookingCreateRequestDTO;
import com.gabriel.moviebooking.dto.booking.BookingResponseDTO;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Reservas", description = "Endpoints para gerenciamento de reservas de ingressos")
public interface BookingControllerDocs {

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
    ResponseEntity<BookingResponseDTO> create(
            @Valid @RequestBody BookingCreateRequestDTO dto,
            @Parameter(hidden = true) Authentication authentication);

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
    ResponseEntity<BookingResponseDTO> confirm(
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication);

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
    ResponseEntity<BookingResponseDTO> cancel(
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication);

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
    ResponseEntity<BookingResponseDTO> findById(@PathVariable Long id);

    @Operation(
            summary = "Minhas reservas",
            description = "Retorna todas as reservas do usuário autenticado."
    )
    @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<List<BookingResponseDTO>> findMyBookings(@Parameter(hidden = true) Authentication authentication);
}