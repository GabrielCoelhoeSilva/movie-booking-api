package com.gabriel.moviebooking.mapper;

import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieResponseDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;
import com.gabriel.moviebooking.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequestDTO dto) {
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDuration(dto.getDuration());
        movie.setGenre(dto.getGenre());
        movie.setAgeRating(dto.getAgeRating());
        return movie;
    }

    public MovieResponseDTO toResponseDTO(Movie movie) {
        MovieResponseDTO dto = new MovieResponseDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setGenre(movie.getGenre());
        dto.setAgeRating(movie.getAgeRating());
        return dto;
    }

    public void updateEntity(Movie movie, MovieUpdateDTO dto) {

        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDuration(dto.getDuration());
        movie.setGenre(dto.getGenre());
        movie.setAgeRating(dto.getAgeRating());
    }
}
