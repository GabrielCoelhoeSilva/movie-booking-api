package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.session.SessionCreateRequestDTO;
import com.gabriel.moviebooking.dto.session.SessionResponseDTO;
import com.gabriel.moviebooking.entity.Movie;
import com.gabriel.moviebooking.entity.Room;
import com.gabriel.moviebooking.entity.Session;
import com.gabriel.moviebooking.exception.BusinessException;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.factory.SeatGenerator;
import com.gabriel.moviebooking.mapper.SessionMapper;
import com.gabriel.moviebooking.repository.MovieRepository;
import com.gabriel.moviebooking.repository.RoomRepository;
import com.gabriel.moviebooking.repository.SessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    @DisplayName("Deve criar uma sessão com sucesso")
    void shouldCreateSessionSuccessfully() {

        // ARRANGE
        SessionCreateRequestDTO dto = new SessionCreateRequestDTO();
        dto.setMovieId(1L);
        dto.setRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2026, 8, 1, 19, 0));
        dto.setPrice(new BigDecimal("25.00"));

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setDuration(120);

        Room room = new Room();
        room.setId(1L);
        room.setCapacity(50);
        room.setSeatsPerRow(10);

        Session savedSession = new Session();
        savedSession.setId(1L);

        SessionResponseDTO responseDTO = new SessionResponseDTO();
        responseDTO.setId(1L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(sessionRepository.existsConflictingSession(any(), any(), any())).thenReturn(false);
        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);
        when(sessionMapper.toResponseDTO(any(Session.class))).thenReturn(responseDTO);

        try (MockedStatic<SeatGenerator> mockedStatic = mockStatic(SeatGenerator.class)) {
            mockedStatic.when(() -> SeatGenerator.generateSeats(anyInt(), anyInt(), any(Session.class)))
                    .thenReturn(List.of());

            // ACT
            SessionResponseDTO result = sessionService.create(dto);

            // ASSERT
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando filme não for encontrado")
    void shouldThrowExceptionWhenMovieNotFound() {

        // ARRANGE
        SessionCreateRequestDTO dto = new SessionCreateRequestDTO();
        dto.setMovieId(99L);
        dto.setRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2026, 8, 1, 19, 0));
        dto.setPrice(new BigDecimal("25.00"));

        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> sessionService.create(dto));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando sala não for encontrada")
    void shouldThrowExceptionWhenRoomNotFound() {

        // ARRANGE
        SessionCreateRequestDTO dto = new SessionCreateRequestDTO();
        dto.setMovieId(1L);
        dto.setRoomId(99L);
        dto.setStartTime(LocalDateTime.of(2026, 8, 1, 19, 0));
        dto.setPrice(new BigDecimal("25.00"));

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setDuration(120);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> sessionService.create(dto));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando houver conflito de horário")
    void shouldThrowExceptionWhenTimeConflict() {

        // ARRANGE
        SessionCreateRequestDTO dto = new SessionCreateRequestDTO();
        dto.setMovieId(1L);
        dto.setRoomId(1L);
        dto.setStartTime(LocalDateTime.of(2026, 8, 1, 19, 0));
        dto.setPrice(new BigDecimal("25.00"));

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setDuration(120);

        Room room = new Room();
        room.setId(1L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(sessionRepository.existsConflictingSession(any(), any(), any())).thenReturn(true);

        // ACT
        Throwable exception = catchThrowable(() -> sessionService.create(dto));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("time range");
    }

    @Test
    @DisplayName("Deve retornar uma sessão quando encontrada por ID")
    void shouldReturnSessionWhenFoundById() {

        // ARRANGE
        Session session = new Session();
        session.setId(1L);

        SessionResponseDTO responseDTO = new SessionResponseDTO();
        responseDTO.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionMapper.toResponseDTO(session)).thenReturn(responseDTO);

        // ACT
        SessionResponseDTO result = sessionService.findById(1L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando sessão não for encontrada por ID")
    void shouldThrowExceptionWhenSessionNotFoundById() {

        // ARRANGE
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> sessionService.findById(99L));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve retornar lista de sessões com sucesso")
    void shouldReturnAllSessions() {

        // ARRANGE
        Session session1 = new Session();
        session1.setId(1L);

        Session session2 = new Session();
        session2.setId(2L);

        SessionResponseDTO dto1 = new SessionResponseDTO();
        dto1.setId(1L);

        SessionResponseDTO dto2 = new SessionResponseDTO();
        dto2.setId(2L);

        when(sessionRepository.findAll()).thenReturn(List.of(session1, session2));
        when(sessionMapper.toResponseDTO(session1)).thenReturn(dto1);
        when(sessionMapper.toResponseDTO(session2)).thenReturn(dto2);

        // ACT
        List<SessionResponseDTO> result = sessionService.findAll();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver sessões")
    void shouldReturnEmptyListWhenNoSessions() {

        // ARRANGE
        when(sessionRepository.findAll()).thenReturn(List.of());

        // ACT
        List<SessionResponseDTO> result = sessionService.findAll();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar uma sessão com sucesso")
    void shouldDeleteSessionSuccessfully() {

        // ARRANGE
        Session session = new Session();
        session.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // ACT
        sessionService.delete(1L);

        // ASSERT
        verify(sessionRepository).delete(session);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar sessão não encontrada")
    void shouldThrowExceptionWhenDeletingNonExistentSession() {

        // ARRANGE
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> sessionService.delete(99L));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}