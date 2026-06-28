package com.gabriel.moviebooking.scheduler;

import com.gabriel.moviebooking.entity.Booking;
import com.gabriel.moviebooking.enums.BookingStatus;
import com.gabriel.moviebooking.repository.BookingRepository;
import com.gabriel.moviebooking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingExpirationScheduler {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    @Scheduled(fixedRate = 60000) // executa a cada 1 minuto
    @Transactional
    public void expireOverdueBookings() {

        List<Booking> expired = bookingRepository.findExpiredBookings(
                BookingStatus.PENDING, LocalDateTime.now());

        for (Booking booking : expired) {
            booking.getSeats().forEach(seat -> seat.setAvailable(true));
            seatRepository.saveAll(booking.getSeats());

            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
        }
    }
}