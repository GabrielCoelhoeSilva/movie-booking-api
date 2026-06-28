package com.gabriel.moviebooking.mapper;

import com.gabriel.moviebooking.dto.room.RoomCreateRequestDTO;
import com.gabriel.moviebooking.dto.room.RoomResponseDTO;
import com.gabriel.moviebooking.entity.Room;
import com.gabriel.moviebooking.enums.RoomType;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomCreateRequestDTO dto) {

        Room room = new Room();
        room.setName(dto.getName());
        room.setCapacity(dto.getCapacity());
        room.setSeatsPerRow(dto.getSeatsPerRow());
        room.setType(RoomType.valueOf(dto.getType().toUpperCase()));
        return room;
    }

    public RoomResponseDTO toResponseDTO(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();

        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setCapacity(room.getCapacity());
        dto.setSeatsPerRow(room.getSeatsPerRow());

        if (room.getType() != null) {
            dto.setType(room.getType().name());
        }

        if (room.getCinema() != null) {
            dto.setCinemaId(room.getCinema().getId());
        }

        return dto;
    }
}