package com.gabriel.moviebooking.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CinemaNotFoundException extends ResourceNotFoundException {

    public CinemaNotFoundException(Long id) {
        super("Cinema não encontrado com id: " + id);
    }
}
