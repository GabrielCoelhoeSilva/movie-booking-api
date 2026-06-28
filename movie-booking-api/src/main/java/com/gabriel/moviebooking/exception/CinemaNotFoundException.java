package com.gabriel.moviebooking.exception;


public class CinemaNotFoundException extends RuntimeException {

    public CinemaNotFoundException(Long id) {
        super("Cinema não encontrado com id: " + id);
    }
}
