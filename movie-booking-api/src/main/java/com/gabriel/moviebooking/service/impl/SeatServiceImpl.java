package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.seat.SeatResponseDTO;
import com.gabriel.moviebooking.mapper.SeatMapper;
import com.gabriel.moviebooking.repository.SeatRepository;
import com.gabriel.moviebooking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    @Override
    public List<SeatResponseDTO> findBySessionId(Long sessionId) {
        return seatRepository.findBySessionId(sessionId)
                .stream()
                .map(seatMapper::toResponseDTO)
                .toList();
    }
}