package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.seat.SeatResponseDTO;
import com.gabriel.moviebooking.entity.Seat;
import com.gabriel.moviebooking.mapper.SeatMapper;
import com.gabriel.moviebooking.repository.SeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatServiceImplTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatMapper seatMapper;

    @InjectMocks
    private SeatServiceImpl seatService;

    @Test
    @DisplayName("Deve retornar lista de assentos de uma sessão")
    void shouldReturnSeatsBySessionId() {

        // ARRANGE
        Seat seat1 = new Seat();
        seat1.setId(1L);
        seat1.setRow("A");
        seat1.setNumber(1);

        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setRow("A");
        seat2.setNumber(2);

        SeatResponseDTO dto1 = new SeatResponseDTO();
        dto1.setId(1L);
        dto1.setRow("A");
        dto1.setNumber(1);

        SeatResponseDTO dto2 = new SeatResponseDTO();
        dto2.setId(2L);
        dto2.setRow("A");
        dto2.setNumber(2);

        when(seatRepository.findBySessionId(1L)).thenReturn(List.of(seat1, seat2));
        when(seatMapper.toResponseDTO(seat1)).thenReturn(dto1);
        when(seatMapper.toResponseDTO(seat2)).thenReturn(dto2);

        // ACT
        List<SeatResponseDTO> result = seatService.findBySessionId(1L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRow()).isEqualTo("A");
        assertThat(result.get(0).getNumber()).isEqualTo(1);
        assertThat(result.get(1).getNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há assentos na sessão")
    void shouldReturnEmptyListWhenNoSeats() {

        // ARRANGE
        when(seatRepository.findBySessionId(99L)).thenReturn(List.of());

        // ACT
        List<SeatResponseDTO> result = seatService.findBySessionId(99L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}