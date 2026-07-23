package com.gabriel.moviebooking.factories;

import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
import com.gabriel.moviebooking.entity.Cinema;
import com.gabriel.moviebooking.enums.State;

public class CinemaFactory {

    public static CinemaCreateDTO createRequestDTO() {
        CinemaCreateDTO dto = new CinemaCreateDTO();

        dto.setName("Cinemark Shopping");
        dto.setCnpj("12345678000199");
        dto.setEmail("cinemark@email.com");
        dto.setPhone("11999999999");
        dto.setStreet("Av. Paulista");
        dto.setNumber("1000");
        dto.setComplement("3º Andar");
        dto.setDistrict("Bela Vista");
        dto.setCity("São Paulo");
        dto.setState(State.SP);
        dto.setZipCode("01310100");

        return dto;
    }

    public static Cinema createCinema() {
        Cinema cinema = new Cinema();

        cinema.setName("Cinemark Shopping");
        cinema.setCnpj("12345678000199");
        cinema.setEmail("cinemark@email.com");
        cinema.setPhone("11999999999");
        cinema.setStreet("Av. Paulista");
        cinema.setNumber("1000");
        cinema.setComplement("3º Andar");
        cinema.setDistrict("Bela Vista");
        cinema.setCity("São Paulo");
        cinema.setState(State.SP);
        cinema.setZipCode("01310100");

        return cinema;
    }

    public static Cinema createSecondCinema() {
        Cinema cinema = new Cinema();

        cinema.setName("UCI Anália Franco");
        cinema.setCnpj("98765432000155");
        cinema.setEmail("uci@email.com");
        cinema.setPhone("11888888888");
        cinema.setStreet("Av. Regente Feijó");
        cinema.setNumber("1739");
        cinema.setComplement("Shopping");
        cinema.setDistrict("Tatuapé");
        cinema.setCity("São Paulo");
        cinema.setState(State.SP);
        cinema.setZipCode("03342000");

        return cinema;
    }

    public static CinemaUpdateDTO createUpdateDTO() {
        CinemaUpdateDTO dto = new CinemaUpdateDTO();

        dto.setName("Kinoplex Paulista");
        dto.setEmail("kinoplex@email.com");
        dto.setPhone("11777777777");
        dto.setStreet("Rua Augusta");
        dto.setNumber("1500");
        dto.setComplement("Loja 2");
        dto.setDistrict("Consolação");
        dto.setCity("São Paulo");
        dto.setState(State.SP);
        dto.setZipCode("01305000");

        return dto;
    }
}