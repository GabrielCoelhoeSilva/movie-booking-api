package com.gabriel.moviebooking.service;

import com.gabriel.moviebooking.dto.booking.BookingCreateRequestDTO;
import com.gabriel.moviebooking.dto.booking.BookingResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BookingService {

    BookingResponseDTO create(BookingCreateRequestDTO dto, Authentication authentication);

    BookingResponseDTO confirm(Long id, Authentication authentication);

    BookingResponseDTO cancel(Long id, Authentication authentication);

    BookingResponseDTO findById(Long id);

    List<BookingResponseDTO> findMyBookings(Authentication authentication);
}