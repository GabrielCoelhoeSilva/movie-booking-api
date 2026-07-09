package com.gabriel.moviebooking.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequestDTO {

    @NotBlank(message = "O email é obrigatório.")
    private String email;

    @NotBlank(message = "O código é obrigatório.")
    private String code;
}