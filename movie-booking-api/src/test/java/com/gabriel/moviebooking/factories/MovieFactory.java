package com.gabriel.moviebooking.factories;

import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;
import com.gabriel.moviebooking.entity.Movie;
import com.gabriel.moviebooking.enums.AgeRating;
import com.gabriel.moviebooking.enums.Genre;

public class MovieFactory {

    public static MovieRequestDTO createRequestDTO() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("O Jogo da Imitação");
        dto.setDescription("Em 1939, a recém-criada agência de inteligência britânica MI6 recruta Alan Turing, um aluno da Universidade de Cambridge, para entender códigos nazistas, incluindo o Enigma, que criptógrafos acreditavam ser inquebrável.");
        dto.setDuration(114);
        dto.setGenre(Genre.DRAMA);
        dto.setAgeRating(AgeRating.TWELVE);
        return dto;
    }

    public static Movie createMovie() {
        Movie movie = new Movie();

        movie.setTitle("O Jogo da Imitação");
        movie.setDescription("Em 1939...");
        movie.setDuration(114);
        movie.setGenre(Genre.DRAMA);
        movie.setAgeRating(AgeRating.TWELVE);

        return movie;
    }

    public static Movie createSecondMovie() {
        Movie movie = new Movie();

        movie.setTitle("Corações de Ferro");
        movie.setDescription("Durante o final da Segunda Guerra Mundial, o sargento Don Wardaddy lidera um grupo de apenas cinco soldados norte-americanos encarregado de aniquilar os nazistas..");
        movie.setDuration(134);
        movie.setGenre(Genre.DRAMA);
        movie.setAgeRating(AgeRating.SIXTEEN);

        return movie;
    }

    public static MovieUpdateDTO createUpdateDTO() {
        MovieUpdateDTO dto = new MovieUpdateDTO();

        dto.setTitle("Interestelar");
        dto.setDescription(
                "Uma equipe de exploradores viaja através de um buraco de minhoca em busca de um novo lar para a humanidade."
        );
        dto.setDuration(169);
        dto.setGenre(Genre.SCI_FI);
        dto.setAgeRating(AgeRating.TEN);

        return dto;
    }
}
