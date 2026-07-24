package com.gabriel.moviebooking.factories;

import com.gabriel.moviebooking.dto.room.RoomCreateRequestDTO;
import com.gabriel.moviebooking.entity.Cinema;
import com.gabriel.moviebooking.entity.Room;
import com.gabriel.moviebooking.enums.RoomType;

public class RoomFactory {

    public static RoomCreateRequestDTO createRequestDTO(Long cinemaId) {
        RoomCreateRequestDTO dto = new RoomCreateRequestDTO();

        dto.setName("Sala 1");
        dto.setCapacity(120);
        dto.setSeatsPerRow(12);
        dto.setCinemaId(cinemaId);
        dto.setType(String.valueOf(RoomType.VIP));

        return dto;
    }

    public static Room createRoom(Cinema cinema) {
        Room room = new Room();

        room.setName("Sala 1");
        room.setCapacity(120);
        room.setSeatsPerRow(12);
        room.setCinema(cinema);
        room.setType(RoomType.IMAX);

        return room;
    }

    public static Room createSecondRoom(Cinema cinema) {
        Room room = new Room();

        room.setName("Sala VIP");
        room.setCapacity(80);
        room.setSeatsPerRow(10);
        room.setCinema(cinema);
        room.setType(RoomType.NORMAL);

        return room;
    }

    public static RoomCreateRequestDTO createUpdateRequestDTO(Long cinemaId) {
        RoomCreateRequestDTO dto = new RoomCreateRequestDTO();

        dto.setName("Sala Premium");
        dto.setCapacity(150);
        dto.setSeatsPerRow(15);
        dto.setCinemaId(cinemaId);
        dto.setType(RoomType.IMAX.name());

        return dto;
    }
}