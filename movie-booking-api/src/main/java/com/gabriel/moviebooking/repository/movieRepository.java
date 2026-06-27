package com.gabriel.moviebooking.repository;

import com.gabriel.moviebooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface movieRepository  extends JpaRepository<Movie, Long> {
}
