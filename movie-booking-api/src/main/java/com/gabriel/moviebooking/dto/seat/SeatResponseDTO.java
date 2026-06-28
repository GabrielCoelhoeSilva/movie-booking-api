package com.gabriel.moviebooking.dto.seat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponseDTO {

    private Long id;
    private String row;
    private Integer number;
    private boolean available;
    private Long sessionId;
}