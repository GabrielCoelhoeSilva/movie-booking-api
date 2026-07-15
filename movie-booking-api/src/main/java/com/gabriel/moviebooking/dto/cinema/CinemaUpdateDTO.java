package com.gabriel.moviebooking.dto.cinema;

import com.gabriel.moviebooking.enums.State;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaUpdateDTO {

    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
    private String name;

    @Email(message = "E-mail inválido")
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 150)
    private String street;

    @Size(max = 20)
    private String number;

    @Size(max = 150)
    private String complement;

    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String city;

    private State state;

    @Pattern(
            regexp = "\\d{8}",
            message = "CEP deve conter exatamente 8 números (sem máscara)"
    )
    private String zipCode;


}
