package com.gabriel.moviebooking.service;

import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieResponseDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;

import java.util.List;

public interface MovieService {

    MovieResponseDTO create(MovieRequestDTO dto);

    MovieResponseDTO findById(Long id);

    List<MovieResponseDTO> findAll();

    MovieResponseDTO update(Long id, MovieUpdateDTO dto);

    void delete(Long id);
}