package com.gabriel.moviebooking.mapper;

import com.gabriel.moviebooking.dto.session.SessionResponseDTO;
import com.gabriel.moviebooking.entity.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public SessionResponseDTO toResponseDTO(Session session) {
        SessionResponseDTO dto = new SessionResponseDTO();

        dto.setId(session.getId());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setPrice(session.getPrice());

        if (session.getMovie() != null) {
            dto.setMovieId(session.getMovie().getId());
            dto.setMovieTitle(session.getMovie().getTitle());
        }

        if (session.getRoom() != null) {
            dto.setRoomId(session.getRoom().getId());
            dto.setRoomName(session.getRoom().getName());
        }

        return dto;
    }
}