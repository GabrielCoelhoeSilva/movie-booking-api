package com.gabriel.moviebooking.service;

import com.gabriel.moviebooking.dto.seat.SeatResponseDTO;

import java.util.List;

public interface SeatService {

    List<SeatResponseDTO> findBySessionId(Long sessionId);
}