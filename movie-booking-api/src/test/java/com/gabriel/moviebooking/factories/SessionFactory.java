package com.gabriel.moviebooking.factories;

import com.gabriel.moviebooking.dto.session.SessionCreateRequestDTO;
import com.gabriel.moviebooking.entity.Movie;
import com.gabriel.moviebooking.entity.Room;
import com.gabriel.moviebooking.entity.Session;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SessionFactory {

    public static SessionCreateRequestDTO createRequestDTO(Long movieId, Long roomId) {
        SessionCreateRequestDTO dto = new SessionCreateRequestDTO();

        dto.setMovieId(movieId);
        dto.setRoomId(roomId);
        dto.setStartTime(LocalDateTime.now().plusDays(1));
        dto.setPrice(BigDecimal.valueOf(35.00));

        return dto;
    }

    public static Session createSession(Movie movie, Room room) {

        Session session = new Session();

        session.setMovie(movie);
        session.setRoom(room);
        session.setStartTime(LocalDateTime.now().plusDays(1));
        session.setEndTime(LocalDateTime.now().plusDays(1).plusMinutes(movie.getDuration()));
        session.setPrice(BigDecimal.valueOf(35.00));

        return session;
    }
}