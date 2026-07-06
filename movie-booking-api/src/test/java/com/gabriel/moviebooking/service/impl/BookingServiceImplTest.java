package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.booking.BookingCreateRequestDTO;
import com.gabriel.moviebooking.dto.booking.BookingResponseDTO;
import com.gabriel.moviebooking.entity.Booking;
import com.gabriel.moviebooking.entity.Seat;
import com.gabriel.moviebooking.entity.Session;
import com.gabriel.moviebooking.entity.User;
import com.gabriel.moviebooking.enums.BookingStatus;
import com.gabriel.moviebooking.enums.Role;
import com.gabriel.moviebooking.exception.BusinessException;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.mapper.BookingMapper;
import com.gabriel.moviebooking.repository.BookingRepository;
import com.gabriel.moviebooking.repository.SeatRepository;
import com.gabriel.moviebooking.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User sampleUser;
    private User adminUser;
    private Session sampleSession;
    private Seat sampleSeat1;
    private Seat sampleSeat2;

    @BeforeEach
    void setUp() {
        // Mock do Usuário Comum
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setRole(Role.CUSTOMER);

        // Mock do Usuário Admin
        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setRole(Role.ADMIN);

        // Mock da Sessão
        sampleSession = new Session();
        sampleSession.setId(10L);
        sampleSession.setPrice(new BigDecimal("30.00"));

        // Mocks dos Assentos associados à Sessão
        sampleSeat1 = new Seat();
        sampleSeat1.setId(100L);
        sampleSeat1.setRow("A");
        sampleSeat1.setNumber(1);
        sampleSeat1.setSession(sampleSession);
        sampleSeat1.setAvailable(true);

        sampleSeat2 = new Seat();
        sampleSeat2.setId(101L);
        sampleSeat2.setRow("A");
        sampleSeat2.setNumber(2);
        sampleSeat2.setSession(sampleSession);
        sampleSeat2.setAvailable(true);
    }

    @Nested
    @DisplayName("Testes de Criação de Reserva (create)")
    class CreateBookingTests {

        @Test
        @DisplayName("Deve criar uma reserva com sucesso quando dados forem válidos")
        void create_ShouldCreateBooking_WhenValidData() {
            // Arrange
            BookingCreateRequestDTO dto = new BookingCreateRequestDTO();
            dto.setSessionId(10L);
            dto.setSeatIds(List.of(100L, 101L));

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(sessionRepository.findById(10L)).thenReturn(Optional.of(sampleSession));
            when(seatRepository.findAllById(dto.getSeatIds())).thenReturn(List.of(sampleSeat1, sampleSeat2));
            when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(new BookingResponseDTO());

            // Act
            BookingResponseDTO result = bookingService.create(dto, authentication);

            // Assert
            assertNotNull(result);
            assertFalse(sampleSeat1.isAvailable());
            assertFalse(sampleSeat2.isAvailable());
            verify(seatRepository, times(1)).saveAll(anyList());
            verify(bookingRepository, times(1)).save(any(Booking.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a sessão não existir")
        void create_ShouldThrowException_WhenSessionNotFound() {
            BookingCreateRequestDTO dto = new BookingCreateRequestDTO();
            dto.setSessionId(99L);

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookingService.create(dto, authentication));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando um ou mais assentos não forem encontrados")
        void create_ShouldThrowException_WhenSomeSeatsNotFound() {
            BookingCreateRequestDTO dto = new BookingCreateRequestDTO();
            dto.setSessionId(10L);
            dto.setSeatIds(List.of(100L, 999L)); // Passou dois IDs

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(sessionRepository.findById(10L)).thenReturn(Optional.of(sampleSession));
            when(seatRepository.findAllById(dto.getSeatIds())).thenReturn(List.of(sampleSeat1)); // Só achou um

            assertThrows(ResourceNotFoundException.class, () -> bookingService.create(dto, authentication));
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando o assento pertencer a outra sessão")
        void create_ShouldThrowException_WhenSeatBelongsToAnotherSession() {
            Session otherSession = new Session();
            otherSession.setId(50L);
            sampleSeat1.setSession(otherSession); // Modifica para outra sessão

            BookingCreateRequestDTO dto = new BookingCreateRequestDTO();
            dto.setSessionId(10L);
            dto.setSeatIds(List.of(100L));

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(sessionRepository.findById(10L)).thenReturn(Optional.of(sampleSession));
            when(seatRepository.findAllById(dto.getSeatIds())).thenReturn(List.of(sampleSeat1));

            assertThrows(BusinessException.class, () -> bookingService.create(dto, authentication));
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando o assento já estiver ocupado")
        void create_ShouldThrowException_WhenSeatIsNotAvailable() {
            sampleSeat1.setAvailable(false); // Assento já ocupado

            BookingCreateRequestDTO dto = new BookingCreateRequestDTO();
            dto.setSessionId(10L);
            dto.setSeatIds(List.of(100L));

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(sessionRepository.findById(10L)).thenReturn(Optional.of(sampleSession));
            when(seatRepository.findAllById(dto.getSeatIds())).thenReturn(List.of(sampleSeat1));

            assertThrows(BusinessException.class, () -> bookingService.create(dto, authentication));
        }
    }

    @Nested
    @DisplayName("Testes de Confirmação de Reserva (confirm)")
    class ConfirmBookingTests {

        @Test
        @DisplayName("Deve confirmar a reserva com sucesso se for o dono e estiver PENDING")
        void confirm_ShouldConfirm_WhenUserIsOwnerAndPending() {
            Booking booking = new Booking();
            booking.setId(500L);
            booking.setUser(sampleUser);
            booking.setStatus(BookingStatus.PENDING);
            booking.setExpiresAt(LocalDateTime.now().plusMinutes(5));

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            bookingService.confirm(500L, authentication);

            assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
            verify(bookingRepository, times(1)).save(booking);
        }

        @Test
        @DisplayName("Deve permitir que um ADMIN confirme qualquer reserva")
        void confirm_ShouldConfirm_WhenUserIsAdmin() {
            Booking booking = new Booking();
            booking.setId(500L);
            booking.setUser(sampleUser); // Reserva pertence ao usuário comum
            booking.setStatus(BookingStatus.PENDING);
            booking.setExpiresAt(LocalDateTime.now().plusMinutes(5));

            when(authentication.getPrincipal()).thenReturn(adminUser); // Logado como ADMIN
            when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            bookingService.confirm(500L, authentication);

            assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        }

        @Test
        @DisplayName("Deve lançar BusinessException se outro usuário tentar confirmar")
        void confirm_ShouldThrowException_WhenUserIsNotOwner() {
            User fraudUser = new User();
            fraudUser.setId(99L);
            fraudUser.setRole(Role.CUSTOMER);

            Booking booking = new Booking();
            booking.setId(500L);
            booking.setUser(sampleUser);

            when(authentication.getPrincipal()).thenReturn(fraudUser);
            when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));

            assertThrows(BusinessException.class, () -> bookingService.confirm(500L, authentication));
        }

        @Test
        @DisplayName("Deve lançar BusinessException se a reserva já não estiver PENDING")
        void confirm_ShouldThrowException_WhenBookingIsNotPending() {
            Booking booking = new Booking();
            booking.setId(500L);
            booking.setUser(sampleUser);
            booking.setStatus(BookingStatus.CONFIRMED); // Já está confirmada

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));

            assertThrows(BusinessException.class, () -> bookingService.confirm(500L, authentication));
        }

        @Test
        @DisplayName("Deve lançar BusinessException se a reserva já estiver expirada")
        void confirm_ShouldThrowException_WhenBookingIsExpired() {
            Booking booking = new Booking();
            booking.setId(500L);
            booking.setUser(sampleUser);
            booking.setStatus(BookingStatus.PENDING);
            booking.setExpiresAt(LocalDateTime.now().minusMinutes(1)); // Expirou a 1 minuto

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));

            assertThrows(BusinessException.class, () -> bookingService.confirm(500L, authentication));
        }
    }

    @Nested
    @DisplayName("Testes de Cancelamento de Reserva (cancel)")
    class CancelBookingTests {

        @Test
        @DisplayName("Deve cancelar reserva e liberar assentos com sucesso")
        void cancel_ShouldCancelAndReleaseSeats() {
            Booking booking = new Booking();
            booking.setId(500L);
            booking.setUser(sampleUser);
            booking.setStatus(BookingStatus.CONFIRMED);
            sampleSeat1.setAvailable(false);
            booking.setSeats(List.of(sampleSeat1));

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

            bookingService.cancel(500L, authentication);

            assertEquals(BookingStatus.CANCELLED, booking.getStatus());
            assertTrue(sampleSeat1.isAvailable()); // Assento deve voltar a ser true
            verify(seatRepository, times(1)).saveAll(booking.getSeats());
            verify(bookingRepository, times(1)).save(booking);
        }

        @Test
        @DisplayName("Deve lançar BusinessException se a reserva já estiver CANCELLED")
        void cancel_ShouldThrowException_WhenAlreadyCancelled() {
            Booking booking = new Booking();
            booking.setId(500L);
            booking.setUser(sampleUser);
            booking.setStatus(BookingStatus.CANCELLED);

            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(bookingRepository.findById(500L)).thenReturn(Optional.of(booking));

            assertThrows(BusinessException.class, () -> bookingService.cancel(500L, authentication));
        }
    }

    @Nested
    @DisplayName("Testes de Consultas (findById / findMyBookings)")
    class QueryBookingTests {

        @Test
        @DisplayName("Deve retornar a lista de reservas do usuário logado")
        void findMyBookings_ShouldReturnUserBookings() {
            when(authentication.getPrincipal()).thenReturn(sampleUser);
            when(bookingRepository.findByUserId(1L)).thenReturn(List.of(new Booking(), new Booking()));
            when(bookingMapper.toResponseDTO(any(Booking.class))).thenReturn(new BookingResponseDTO());

            List<BookingResponseDTO> result = bookingService.findMyBookings(authentication);

            assertEquals(2, result.size());
            verify(bookingRepository, times(1)).findByUserId(1L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
        void findById_ShouldThrowException_WhenIdDoesNotExist() {
            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> bookingService.findById(999L));
        }
    }
}