package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaResponseDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
import com.gabriel.moviebooking.entity.Cinema;
import com.gabriel.moviebooking.exception.CinemaNotFoundException;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.mapper.CinemaMapper;
import com.gabriel.moviebooking.repository.CinemaRepository;
import com.gabriel.moviebooking.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {

    private final CinemaRepository repository;
    private final CinemaMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = "cinemas", allEntries = true)
    public CinemaResponseDTO create(final CinemaCreateDTO dto) {
        Cinema cinema = mapper.toEntity(dto);
        Cinema savedCinema = repository.save(cinema);
        return mapper.toResponseDTO(savedCinema);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cinemas", key = "#id")
    public CinemaResponseDTO findById(final Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new CinemaNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cinemas")
    public List<CinemaResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "cinemas", allEntries = true)
    public CinemaResponseDTO update(final Long id, final CinemaUpdateDTO dto) {
        Cinema cinema = repository.findById(id)
                .orElseThrow(() -> new CinemaNotFoundException(id));

        mapper.updateEntity(cinema, dto);
        Cinema updatedCinema = repository.save(cinema);

        return mapper.toResponseDTO(updatedCinema);
    }

    @Override
    @Transactional
    @CacheEvict(value = "cinemas", allEntries = true)
    public void delete(final Long id) {
        if (!repository.existsById(id)) {
            throw new CinemaNotFoundException(id);
        }
        repository.deleteById(id);
    }
}