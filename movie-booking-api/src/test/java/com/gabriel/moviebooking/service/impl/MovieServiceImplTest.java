package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieResponseDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;
import com.gabriel.moviebooking.entity.Movie;
import com.gabriel.moviebooking.enums.AgeRating;
import com.gabriel.moviebooking.enums.Genre;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.mapper.MovieMapper;
import com.gabriel.moviebooking.repository.MovieRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    @DisplayName("Deve criar um filme com sucesso")
    void deveCriarFilmecomSucesso () {

        //Arrange (Preparação)
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Vingadores: Guerra Infinita");
        dto.setDescription("Homem de Ferro, Capitão América, Thor, Hulk e os Vingadores se unem para combater o maligno Thanos. Em uma missão para coletar todas as seis pedras infinitas, Thanos planeja usá-las para infligir sua vontade maléfica sobre a humanidade.");
        dto.setDuration(149);
        dto.setGenre(Genre.ACTION);
        dto.setAgeRating(AgeRating.TWELVE);

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Vingadores: Guerra Infinita");

        MovieResponseDTO responseDTO = new MovieResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("Vingadores: Guerra Infinita");

        when(movieMapper.toEntity(dto)).thenReturn(movie);
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toResponseDTO(movie)). thenReturn(responseDTO);

        //Act (Execução)
        MovieResponseDTO result = movieService.create(dto);

        //Assert (Verificação)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Vingadores: Guerra Infinita");


    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando filme não for encontrado")
    void deveLancarExcecaoFilmenaoEncontrado() {

        //Arrange (Preparação)
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        //Act (Execução)
        Throwable exception = catchThrowable(() -> movieService.findById(99L));

        //Assert (Verificação)
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");


    }

    @Test
    @DisplayName("Deve retornar um filme quando encontrado por ID")
    void deveRetornarFilmeEncontradoporId() {

        //Arrange (Preparação)
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");

        MovieResponseDTO responseDTO = new MovieResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("Inception");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieMapper.toResponseDTO(movie)).thenReturn(responseDTO);

        //Act (Execução)
        MovieResponseDTO result = movieService.findById(1L);

        //Assert (Verificação)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Inception");
    }

    @Test
    @DisplayName("Deve retornar lista de filmes com sucesso")
    void shouldReturnAllMovies() {

        // ARRANGE
        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("Inception");

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle("Interstellar");

        MovieResponseDTO dto1 = new MovieResponseDTO();
        dto1.setId(1L);
        dto1.setTitle("Inception");

        MovieResponseDTO dto2 = new MovieResponseDTO();
        dto2.setId(2L);
        dto2.setTitle("Interstellar");

        when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2));
        when(movieMapper.toResponseDTO(movie1)).thenReturn(dto1);
        when(movieMapper.toResponseDTO(movie2)).thenReturn(dto2);

        // ACT
        List<MovieResponseDTO> result = movieService.findAll();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Inception");
        assertThat(result.get(1).getTitle()).isEqualTo("Interstellar");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver filmes")
    void shouldReturnEmptyListWhenNoMovies() {

        // ARRANGE
        when(movieRepository.findAll()).thenReturn(List.of());

        // ACT
        List<MovieResponseDTO> result = movieService.findAll();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar um filme com sucesso")
    void shouldUpdateMovieSuccessfully() {

        // ARRANGE
        MovieUpdateDTO dto = new MovieUpdateDTO();
        dto.setTitle("Inception 2");
        dto.setDescription("A continuação");
        dto.setDuration(160);
        dto.setGenre(Genre.ACTION);
        dto.setAgeRating(AgeRating.FOURTEEN);

        Movie existingMovie = new Movie();
        existingMovie.setId(1L);
        existingMovie.setTitle("Inception");

        MovieResponseDTO responseDTO = new MovieResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("Inception 2");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(existingMovie);
        when(movieMapper.toResponseDTO(existingMovie)).thenReturn(responseDTO);

        // ACT
        MovieResponseDTO result = movieService.update(1L, dto);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Inception 2");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar filme não encontrado")
    void shouldThrowExceptionWhenUpdatingNonExistentMovie() {

        // ARRANGE
        MovieUpdateDTO dto = new MovieUpdateDTO();
        dto.setTitle("Inception 2");

        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> movieService.update(99L, dto));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve deletar um filme com sucesso")
    void shouldDeleteMovieSuccessfully() {

        // ARRANGE
        when(movieRepository.existsById(1L)).thenReturn(true);

        // ACT
        movieService.delete(1L);

        // ASSERT
        verify(movieRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar filme não encontrado")
    void shouldThrowExceptionWhenDeletingNonExistentMovie() {

        // ARRANGE
        when(movieRepository.existsById(99L)).thenReturn(false);

        // ACT
        Throwable exception = catchThrowable(() -> movieService.delete(99L));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}