package com.gabriel.moviebooking.dto.movie;

import com.gabriel.moviebooking.enums.AgeRating;
import com.gabriel.moviebooking.enums.Genre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {

    @NotBlank(message = "Title is required.")
    @Size(max = 150, message = "Title can have at most 150 characters.")
    private String title;

    @NotBlank(message = "Description is required.")
    @Size(max = 1000, message = "Description can have at most 1000 characters.")
    private String description;

    @NotNull(message = "Duration is required.")
    @Min(value = 1, message = "Duration must be at least 1 minute.")
    private Integer duration;

    @NotNull(message = "Genre is required.")
    private Genre genre;

    @NotNull(message = "Age rating is required.")
    private AgeRating ageRating;

}
