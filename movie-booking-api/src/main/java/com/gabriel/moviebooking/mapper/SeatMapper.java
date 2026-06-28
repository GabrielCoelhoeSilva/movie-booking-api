package com.gabriel.moviebooking.mapper;

import com.gabriel.moviebooking.dto.seat.SeatResponseDTO;
import com.gabriel.moviebooking.entity.Seat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public SeatResponseDTO toResponseDTO(Seat seat) {
        SeatResponseDTO dto = new SeatResponseDTO();

        dto.setId(seat.getId());
        dto.setRow(seat.getRow());
        dto.setNumber(seat.getNumber());
        dto.setAvailable(seat.isAvailable());

        if (seat.getSession() != null) {
            dto.setSessionId(seat.getSession().getId());
        }

        return dto;
    }
}