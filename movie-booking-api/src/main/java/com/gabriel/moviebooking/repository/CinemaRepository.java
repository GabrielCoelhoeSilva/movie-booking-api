package com.gabriel.moviebooking.repository;

import com.gabriel.moviebooking.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
}
