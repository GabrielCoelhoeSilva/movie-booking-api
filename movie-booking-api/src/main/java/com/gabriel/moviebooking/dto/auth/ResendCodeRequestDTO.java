package com.gabriel.moviebooking.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResendCodeRequestDTO {

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Email inválido.")
    private String email;
}