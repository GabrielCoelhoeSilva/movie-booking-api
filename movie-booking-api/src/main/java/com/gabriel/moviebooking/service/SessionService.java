package com.gabriel.moviebooking.service;

import com.gabriel.moviebooking.dto.session.SessionCreateRequestDTO;
import com.gabriel.moviebooking.dto.session.SessionResponseDTO;

import java.util.List;

public interface SessionService {

    SessionResponseDTO create(SessionCreateRequestDTO dto);

    SessionResponseDTO findById(Long id);

    List<SessionResponseDTO> findAll();

    void delete(Long id);

}