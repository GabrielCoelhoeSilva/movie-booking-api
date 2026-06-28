package com.gabriel.moviebooking.dto.booking;

import com.gabriel.moviebooking.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookingResponseDTO {

    private Long id;
    private Long sessionId;
    private String movieTitle;
    private String roomName;
    private String userEmail;
    private BookingStatus status;
    private List<String> seats;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}