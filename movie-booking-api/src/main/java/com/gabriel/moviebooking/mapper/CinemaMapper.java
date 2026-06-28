package com.gabriel.moviebooking.mapper;

import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaResponseDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
import com.gabriel.moviebooking.entity.Cinema;
import org.springframework.stereotype.Component;

@Component
public class CinemaMapper {

    public Cinema toEntity(CinemaCreateDTO dto) {
        Cinema cinema = new Cinema();

        cinema.setName(dto.getName());
        cinema.setCnpj(dto.getCnpj());
        cinema.setEmail(dto.getEmail());
        cinema.setPhone(dto.getPhone());
        cinema.setStreet(dto.getStreet());
        cinema.setNumber(dto.getNumber());
        cinema.setComplement(dto.getComplement());
        cinema.setDistrict(dto.getDistrict());
        cinema.setCity(dto.getCity());
        cinema.setState(dto.getState());
        cinema.setZipCode(dto.getZipCode());

        return cinema;
    }

    public void updateEntity(Cinema cinema, CinemaUpdateDTO dto) {

        cinema.setName(dto.getName());
        cinema.setEmail(dto.getEmail());
        cinema.setPhone(dto.getPhone());
        cinema.setStreet(dto.getStreet());
        cinema.setNumber(dto.getNumber());
        cinema.setComplement(dto.getComplement());
        cinema.setDistrict(dto.getDistrict());
        cinema.setCity(dto.getCity());
        cinema.setState(dto.getState());
        cinema.setZipCode(dto.getZipCode());
    }

    public CinemaResponseDTO toResponseDTO(Cinema cinema) {
        CinemaResponseDTO dto = new CinemaResponseDTO();

        dto.setId(cinema.getId());
        dto.setName(cinema.getName());
        dto.setCnpj(cinema.getCnpj());
        dto.setEmail(cinema.getEmail());
        dto.setPhone(cinema.getPhone());
        dto.setStreet(cinema.getStreet());
        dto.setNumber(cinema.getNumber());
        dto.setComplement(cinema.getComplement());
        dto.setDistrict(cinema.getDistrict());
        dto.setCity(cinema.getCity());
        dto.setState(cinema.getState());
        dto.setZipCode(cinema.getZipCode());

        return dto;
    }
}
