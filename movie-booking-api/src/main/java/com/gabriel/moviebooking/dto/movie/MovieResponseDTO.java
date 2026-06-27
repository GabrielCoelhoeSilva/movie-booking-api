package com.gabriel.moviebooking.dto.movie;

import com.gabriel.moviebooking.enums.AgeRating;
import com.gabriel.moviebooking.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDTO {

    private Long id;

    private String title;

    private String description;

    private Integer duration;

    private Genre genre;

    private AgeRating ageRating;

}