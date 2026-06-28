package com.gabriel.moviebooking.dto.room;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDTO {

    Long id;
    String name;
    Integer capacity;
    String type;
    Long cinemaId;
}
