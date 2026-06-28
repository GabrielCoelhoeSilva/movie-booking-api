package com.gabriel.moviebooking.dto.cinema;

import com.gabriel.moviebooking.enums.State;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaResponseDTO {

    private Long id;

    private String name;
    private String cnpj;
    private String email;
    private String phone;

    private String street;
    private String number;
    private String complement;
    private String district;
    private String city;
    private State state;
    private String zipCode;
}
