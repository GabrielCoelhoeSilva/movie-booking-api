package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.session.SessionCreateRequestDTO;
import com.gabriel.moviebooking.dto.session.SessionResponseDTO;
import com.gabriel.moviebooking.entity.Movie;
import com.gabriel.moviebooking.entity.Room;
import com.gabriel.moviebooking.entity.Seat;
import com.gabriel.moviebooking.entity.Session;
import com.gabriel.moviebooking.exception.BusinessException;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.factory.SeatGenerator;
import com.gabriel.moviebooking.mapper.SessionMapper;
import com.gabriel.moviebooking.repository.MovieRepository;
import com.gabriel.moviebooking.repository.RoomRepository;
import com.gabriel.moviebooking.repository.SessionRepository;
import com.gabriel.moviebooking.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final SessionMapper sessionMapper;

    @Override
    @Transactional
    public SessionResponseDTO create(SessionCreateRequestDTO dto) {

        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Movie not found with id: " + dto.getMovieId()));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + dto.getRoomId()));

        LocalDateTime startTime = dto.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(movie.getDuration());

        boolean hasConflict = sessionRepository.existsConflictingSession(
                room.getId(), startTime, endTime);

        if (hasConflict) {
            throw new BusinessException(
                    "Room already has a session scheduled in this time range");
        }

        Session session = new Session();
        session.setMovie(movie);
        session.setRoom(room);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setPrice(dto.getPrice());

        Session savedSession = sessionRepository.save(session);

        List<Seat> seats = SeatGenerator.generateSeats(
                room.getCapacity(), room.getSeatsPerRow(), savedSession);

        savedSession.setSeats(seats);
        sessionRepository.save(savedSession);

        return sessionMapper.toResponseDTO(savedSession);
    }

    @Override
    public SessionResponseDTO findById(Long id) {
        Session session = findEntityById(id);
        return sessionMapper.toResponseDTO(session);
    }

    @Override
    public List<SessionResponseDTO> findAll() {
        return sessionRepository.findAll()
                .stream()
                .map(sessionMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Session session = findEntityById(id);
        sessionRepository.delete(session);
    }

    private Session findEntityById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + id));
    }
}