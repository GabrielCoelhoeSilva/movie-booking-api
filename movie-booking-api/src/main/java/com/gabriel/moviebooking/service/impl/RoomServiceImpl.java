package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.room.RoomCreateRequestDTO;
import com.gabriel.moviebooking.dto.room.RoomResponseDTO;
import com.gabriel.moviebooking.entity.Cinema;
import com.gabriel.moviebooking.entity.Room;
import com.gabriel.moviebooking.exception.BusinessException;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.mapper.RoomMapper;
import com.gabriel.moviebooking.repository.CinemaRepository;
import com.gabriel.moviebooking.repository.RoomRepository;
import com.gabriel.moviebooking.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;
    private final RoomMapper roomMapper;

    public RoomServiceImpl(RoomRepository roomRepository,
                           CinemaRepository cinemaRepository,
                           RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
        this.roomMapper = roomMapper;
    }

    @Override
    @Transactional
    public RoomResponseDTO create(RoomCreateRequestDTO dto) {
        Room room = roomMapper.toEntity(dto);

        Cinema cinema = cinemaRepository.findById(dto.getCinemaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cinema not found with id: " + dto.getCinemaId()));
        room.setCinema(cinema);

        validateRoom(room);

        Room savedRoom = roomRepository.save(room);
        return roomMapper.toResponseDTO(savedRoom);
    }

    @Override
    @Transactional
    public RoomResponseDTO update(Long id, RoomCreateRequestDTO dto) {
        Room existingRoom = findEntityById(id);

        Cinema cinema = cinemaRepository.findById(dto.getCinemaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cinema not found with id: " + dto.getCinemaId()));

        existingRoom.setName(dto.getName());
        existingRoom.setCapacity(dto.getCapacity());
        existingRoom.setType(roomMapper.toEntity(dto).getType());
        existingRoom.setCinema(cinema);
        existingRoom.setSeatsPerRow(dto.getSeatsPerRow());

        validateRoom(existingRoom);

        Room updatedRoom = roomRepository.save(existingRoom);
        return roomMapper.toResponseDTO(updatedRoom);
    }

    @Override
    public RoomResponseDTO findById(Long id) {
        return roomMapper.toResponseDTO(findEntityById(id));
    }

    @Override
    public List<RoomResponseDTO> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Room room = findEntityById(id);
        roomRepository.delete(room);
    }

    private Room findEntityById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    private void validateRoom(Room room) {
        if (room.getName() == null || room.getName().isBlank()) {
            throw new BusinessException("Room name cannot be empty");
        }

        if (room.getCapacity() == null || room.getCapacity() <= 0) {
            throw new BusinessException("Room capacity must be greater than 0");
        }

        if (room.getCinema() == null) {
            throw new BusinessException("Room must be linked to a cinema");
        }

        if (room.getSeatsPerRow() == null || room.getSeatsPerRow() <= 0) {
            throw new BusinessException("Seats per row must be greater than 0");
        }
    }
}