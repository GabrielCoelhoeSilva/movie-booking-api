package com.gabriel.moviebooking.mapper;

import com.gabriel.moviebooking.dto.booking.BookingResponseDTO;
import com.gabriel.moviebooking.entity.Booking;
import com.gabriel.moviebooking.entity.Seat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public BookingResponseDTO toResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();

        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setExpiresAt(booking.getExpiresAt());

        if (booking.getUser() != null) {
            dto.setUserEmail(booking.getUser().getEmail());
        }

        if (booking.getSession() != null) {
            dto.setSessionId(booking.getSession().getId());
            if (booking.getSession().getMovie() != null) {
                dto.setMovieTitle(booking.getSession().getMovie().getTitle());
            }
            if (booking.getSession().getRoom() != null) {
                dto.setRoomName(booking.getSession().getRoom().getName());
            }
        }

        if (booking.getSeats() != null) {
            List<String> seatLabels = booking.getSeats().stream()
                    .map(seat -> seat.getRow() + seat.getNumber())
                    .toList();
            dto.setSeats(seatLabels);
        }

        return dto;
    }
}