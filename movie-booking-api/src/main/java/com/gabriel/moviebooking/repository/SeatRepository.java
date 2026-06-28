package com.gabriel.moviebooking.repository;

import com.gabriel.moviebooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findBySessionId(Long sessionId);
}