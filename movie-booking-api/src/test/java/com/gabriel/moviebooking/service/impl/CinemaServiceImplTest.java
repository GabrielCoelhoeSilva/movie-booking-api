package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaResponseDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
import com.gabriel.moviebooking.entity.Cinema;
import com.gabriel.moviebooking.exception.CinemaNotFoundException;
import com.gabriel.moviebooking.mapper.CinemaMapper;
import com.gabriel.moviebooking.repository.CinemaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CinemaServiceImplTest {

    @Mock
    private CinemaRepository repository;

    @Mock
    private CinemaMapper mapper;

    @InjectMocks
    private CinemaServiceImpl cinemaService;

    @Test
    @DisplayName("Deve criar um cinema com sucesso")
    void shouldCreateCinemaSuccessfully() {

        // ARRANGE
        CinemaCreateDTO dto = new CinemaCreateDTO();
        dto.setName("CineStar Paulista");

        Cinema cinema = new Cinema();
        cinema.setId(1L);
        cinema.setName("CineStar Paulista");

        CinemaResponseDTO responseDTO = new CinemaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("CineStar Paulista");

        when(mapper.toEntity(dto)).thenReturn(cinema);
        when(repository.save(any(Cinema.class))).thenReturn(cinema);
        when(mapper.toResponseDTO(cinema)).thenReturn(responseDTO);

        // ACT
        CinemaResponseDTO result = cinemaService.create(dto);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("CineStar Paulista");
    }

    @Test
    @DisplayName("Deve retornar um cinema quando encontrado por ID")
    void shouldReturnCinemaWhenFoundById() {

        // ARRANGE
        Cinema cinema = new Cinema();
        cinema.setId(1L);
        cinema.setName("CineStar Paulista");

        CinemaResponseDTO responseDTO = new CinemaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("CineStar Paulista");

        when(repository.findById(1L)).thenReturn(Optional.of(cinema));
        when(mapper.toResponseDTO(cinema)).thenReturn(responseDTO);

        // ACT
        CinemaResponseDTO result = cinemaService.findById(1L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("CineStar Paulista");
    }

    @Test
    @DisplayName("Deve lançar CinemaNotFoundException quando cinema não for encontrado por ID")
    void shouldThrowExceptionWhenCinemaNotFoundById() {

        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> cinemaService.findById(99L));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(CinemaNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve retornar lista de cinemas com sucesso")
    void shouldReturnAllCinemas() {

        // ARRANGE
        Cinema cinema1 = new Cinema();
        cinema1.setId(1L);
        cinema1.setName("CineStar Paulista");

        Cinema cinema2 = new Cinema();
        cinema2.setId(2L);
        cinema2.setName("Cinemax Boulevard");

        CinemaResponseDTO dto1 = new CinemaResponseDTO();
        dto1.setId(1L);
        dto1.setName("CineStar Paulista");

        CinemaResponseDTO dto2 = new CinemaResponseDTO();
        dto2.setId(2L);
        dto2.setName("Cinemax Boulevard");

        when(repository.findAll()).thenReturn(List.of(cinema1, cinema2));
        when(mapper.toResponseDTO(cinema1)).thenReturn(dto1);
        when(mapper.toResponseDTO(cinema2)).thenReturn(dto2);

        // ACT
        List<CinemaResponseDTO> result = cinemaService.findAll();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("CineStar Paulista");
        assertThat(result.get(1).getName()).isEqualTo("Cinemax Boulevard");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver cinemas")
    void shouldReturnEmptyListWhenNoCinemas() {

        // ARRANGE
        when(repository.findAll()).thenReturn(List.of());

        // ACT
        List<CinemaResponseDTO> result = cinemaService.findAll();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar um cinema com sucesso")
    void shouldUpdateCinemaSuccessfully() {

        // ARRANGE
        CinemaUpdateDTO dto = new CinemaUpdateDTO();
        dto.setName("CineStar Atualizado");

        Cinema existingCinema = new Cinema();
        existingCinema.setId(1L);
        existingCinema.setName("CineStar Paulista");

        CinemaResponseDTO responseDTO = new CinemaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("CineStar Atualizado");

        when(repository.findById(1L)).thenReturn(Optional.of(existingCinema));
        when(repository.save(any(Cinema.class))).thenReturn(existingCinema);
        when(mapper.toResponseDTO(existingCinema)).thenReturn(responseDTO);

        // ACT
        CinemaResponseDTO result = cinemaService.update(1L, dto);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("CineStar Atualizado");
    }

    @Test
    @DisplayName("Deve lançar CinemaNotFoundException ao atualizar cinema não encontrado")
    void shouldThrowExceptionWhenUpdatingNonExistentCinema() {

        // ARRANGE
        CinemaUpdateDTO dto = new CinemaUpdateDTO();
        dto.setName("CineStar Atualizado");

        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> cinemaService.update(99L, dto));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(CinemaNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve deletar um cinema com sucesso")
    void shouldDeleteCinemaSuccessfully() {

        // ARRANGE
        when(repository.existsById(1L)).thenReturn(true);

        // ACT
        cinemaService.delete(1L);

        // ASSERT
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar CinemaNotFoundException ao deletar cinema não encontrado")
    void shouldThrowExceptionWhenDeletingNonExistentCinema() {

        // ARRANGE
        when(repository.existsById(99L)).thenReturn(false);

        // ACT
        Throwable exception = catchThrowable(() -> cinemaService.delete(99L));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(CinemaNotFoundException.class)
                .hasMessageContaining("99");
    }
}