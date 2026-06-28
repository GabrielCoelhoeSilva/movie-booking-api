package com.gabriel.moviebooking.repository;

import com.gabriel.moviebooking.entity.Booking;
import com.gabriel.moviebooking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.status = :status
            AND b.expiresAt < :now
            """)
    List<Booking> findExpiredBookings(
            @Param("status") BookingStatus status,
            @Param("now") LocalDateTime now
    );
}