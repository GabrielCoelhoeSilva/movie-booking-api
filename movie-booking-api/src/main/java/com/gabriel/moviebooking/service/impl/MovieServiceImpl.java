package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieResponseDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;
import com.gabriel.moviebooking.entity.Movie;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.mapper.MovieMapper;
import com.gabriel.moviebooking.service.MovieService;
import com.gabriel.moviebooking.repository.MovieRepository;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository repository;
    private final MovieMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = "movies", allEntries = true)
    public MovieResponseDTO create(MovieRequestDTO dto) {
        Movie movie = mapper.toEntity(dto);

        Movie savedMovie = repository.save(movie);

        return mapper.toResponseDTO(savedMovie);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "movies", key = "#id")
    public MovieResponseDTO findById(Long id) {

        Movie movie = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Movie not found with id: " + id));

        return mapper.toResponseDTO(movie);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "movies")
    public List<MovieResponseDTO> findAll() {

        List<Movie> movies = repository.findAll();

        return movies.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "movies", allEntries = true)
    public MovieResponseDTO update(Long id, MovieUpdateDTO dto) {

        Movie movie = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Movie not found with id: " + id));

        mapper.updateEntity(movie, dto);

        Movie updatedMovie = repository.save(movie);

        return mapper.toResponseDTO(updatedMovie);
    }

    @Override
    @Transactional
    @CacheEvict(value = "movies", allEntries = true)
    public void delete(final Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Movie not found with id" + id);
        }
        repository.deleteById(id);
    }
}