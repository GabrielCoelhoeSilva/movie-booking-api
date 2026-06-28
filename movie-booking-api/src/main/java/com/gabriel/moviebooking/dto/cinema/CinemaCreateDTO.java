package com.gabriel.moviebooking.dto.cinema;

import com.gabriel.moviebooking.enums.State;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaCreateDTO {

    @NotBlank(message = "O nome do cinema é obrigatório")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
    private String name;

    @NotBlank(message = "O CNPJ é obrigatório")
    @Pattern(
            regexp = "\\d{14}",
            message = "CNPJ deve conter exatamente 14 números (sem máscara)"
    )
    private String cnpj;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "A rua é obrigatória")
    @Size(max = 150)
    private String street;

    @NotBlank(message = "O número é obrigatório")
    @Size(max = 20)
    private String number;

    @Size(max = 150)
    private String complement;

    @NotBlank(message = "O bairro é obrigatório")
    @Size(max = 100)
    private String district;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 100)
    private String city;

    @NotNull(message = "O estado é obrigatório")
    private State state;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(
            regexp = "\\d{8}",
            message = "CEP deve conter exatamente 8 números (sem máscara)"
    )
    private String zipCode;


}
