package com.gabriel.moviebooking.service;

import com.gabriel.moviebooking.dto.room.RoomCreateRequestDTO;
import com.gabriel.moviebooking.dto.room.RoomResponseDTO;

import java.util.List;

public interface RoomService {

    RoomResponseDTO create(RoomCreateRequestDTO dto);

    RoomResponseDTO update(Long id, RoomCreateRequestDTO dto);

    RoomResponseDTO findById(Long id);

    List<RoomResponseDTO> findAll();

    void delete(Long id);
}