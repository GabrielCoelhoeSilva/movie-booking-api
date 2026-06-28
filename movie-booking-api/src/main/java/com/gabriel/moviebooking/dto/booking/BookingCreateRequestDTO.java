package com.gabriel.moviebooking.dto.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingCreateRequestDTO {

    @NotNull(message = "Session id is required")
    private Long sessionId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<Long> seatIds;
}