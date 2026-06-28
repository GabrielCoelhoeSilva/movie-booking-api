package com.gabriel.moviebooking.service;

import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaResponseDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;

import java.util.List;

public interface CinemaService {

    CinemaResponseDTO create(CinemaCreateDTO dto);

    CinemaResponseDTO findById(Long id);

    List<CinemaResponseDTO> findAll();

    CinemaResponseDTO update(Long id, CinemaUpdateDTO dto);

    void delete(Long id);
}
