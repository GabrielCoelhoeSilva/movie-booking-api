package com.gabriel.moviebooking.dto.room;

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
public class RoomUpdateDTO {
    @NotBlank(message = "O nome da sala é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    String name;

    @NotNull(message = "A capacidade da sala é obrigatória.")
    @Min(value = 10, message = "A capacidade mínima permitida é de 10 assentos.")
    Integer capacity;

    @NotNull(message = "O ID do cinema é obrigatório.")
    Long cinemaId;

    @NotNull(message = "O tipo de sala (RoomType) é obrigatório.")
    String type;
}

