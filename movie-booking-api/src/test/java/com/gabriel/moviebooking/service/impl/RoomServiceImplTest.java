package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.room.RoomCreateRequestDTO;
import com.gabriel.moviebooking.dto.room.RoomResponseDTO;
import com.gabriel.moviebooking.entity.Cinema;
import com.gabriel.moviebooking.entity.Room;
import com.gabriel.moviebooking.enums.RoomType;
import com.gabriel.moviebooking.exception.BusinessException;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.mapper.RoomMapper;
import com.gabriel.moviebooking.repository.CinemaRepository;
import com.gabriel.moviebooking.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Test
    @DisplayName("Deve criar uma sala com sucesso")
    void shouldCreateRoomSuccessfully() {

        // ARRANGE
        RoomCreateRequestDTO dto = new RoomCreateRequestDTO();
        dto.setName("Sala 1");
        dto.setCapacity(50);
        dto.setSeatsPerRow(10);
        dto.setCinemaId(1L);
        dto.setType("STANDARD");

        Cinema cinema = new Cinema();
        cinema.setId(1L);
        cinema.setName("CineStar");

        Room room = new Room();
        room.setName("Sala 1");
        room.setCapacity(50);
        room.setSeatsPerRow(10);
        room.setType(RoomType.STANDARD);

        Room savedRoom = new Room();
        savedRoom.setId(1L);
        savedRoom.setName("Sala 1");
        savedRoom.setCapacity(50);
        savedRoom.setSeatsPerRow(10);
        savedRoom.setCinema(cinema);

        RoomResponseDTO responseDTO = new RoomResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Sala 1");

        when(roomMapper.toEntity(dto)).thenReturn(room);
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));
        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);
        when(roomMapper.toResponseDTO(savedRoom)).thenReturn(responseDTO);

        // ACT
        RoomResponseDTO result = roomService.create(dto);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Sala 1");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando cinema não for encontrado ao criar sala")
    void shouldThrowExceptionWhenCinemaNotFoundOnCreate() {

        // ARRANGE
        RoomCreateRequestDTO dto = new RoomCreateRequestDTO();
        dto.setName("Sala 1");
        dto.setCapacity(50);
        dto.setSeatsPerRow(10);
        dto.setCinemaId(99L);
        dto.setType("STANDARD");

        Room room = new Room();
        room.setName("Sala 1");
        room.setCapacity(50);
        room.setSeatsPerRow(10);

        when(roomMapper.toEntity(dto)).thenReturn(room);
        when(cinemaRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> roomService.create(dto));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando nome da sala estiver vazio")
    void shouldThrowExceptionWhenRoomNameIsEmpty() {

        // ARRANGE
        RoomCreateRequestDTO dto = new RoomCreateRequestDTO();
        dto.setName("");
        dto.setCapacity(50);
        dto.setSeatsPerRow(10);
        dto.setCinemaId(1L);
        dto.setType("STANDARD");

        Cinema cinema = new Cinema();
        cinema.setId(1L);

        Room room = new Room();
        room.setName("");
        room.setCapacity(50);
        room.setSeatsPerRow(10);
        room.setCinema(cinema);

        when(roomMapper.toEntity(dto)).thenReturn(room);
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));

        // ACT
        Throwable exception = catchThrowable(() -> roomService.create(dto));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("name cannot be empty");
    }

    @Test
    @DisplayName("Deve retornar uma sala quando encontrada por ID")
    void shouldReturnRoomWhenFoundById() {

        // ARRANGE
        Cinema cinema = new Cinema();
        cinema.setId(1L);

        Room room = new Room();
        room.setId(1L);
        room.setName("Sala 1");
        room.setCinema(cinema);

        RoomResponseDTO responseDTO = new RoomResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Sala 1");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomMapper.toResponseDTO(room)).thenReturn(responseDTO);

        // ACT
        RoomResponseDTO result = roomService.findById(1L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Sala 1");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando sala não for encontrada por ID")
    void shouldThrowExceptionWhenRoomNotFoundById() {

        // ARRANGE
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> roomService.findById(99L));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve retornar lista de salas com sucesso")
    void shouldReturnAllRooms() {

        // ARRANGE
        Room room1 = new Room();
        room1.setId(1L);
        room1.setName("Sala 1");

        Room room2 = new Room();
        room2.setId(2L);
        room2.setName("Sala 2");

        RoomResponseDTO dto1 = new RoomResponseDTO();
        dto1.setId(1L);
        dto1.setName("Sala 1");

        RoomResponseDTO dto2 = new RoomResponseDTO();
        dto2.setId(2L);
        dto2.setName("Sala 2");

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));
        when(roomMapper.toResponseDTO(room1)).thenReturn(dto1);
        when(roomMapper.toResponseDTO(room2)).thenReturn(dto2);

        // ACT
        List<RoomResponseDTO> result = roomService.findAll();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Sala 1");
        assertThat(result.get(1).getName()).isEqualTo("Sala 2");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver salas")
    void shouldReturnEmptyListWhenNoRooms() {

        // ARRANGE
        when(roomRepository.findAll()).thenReturn(List.of());

        // ACT
        List<RoomResponseDTO> result = roomService.findAll();

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar uma sala com sucesso")
    void shouldUpdateRoomSuccessfully() {

        // ARRANGE
        RoomCreateRequestDTO dto = new RoomCreateRequestDTO();
        dto.setName("Sala Atualizada");
        dto.setCapacity(60);
        dto.setSeatsPerRow(10);
        dto.setCinemaId(1L);
        dto.setType("STANDARD");

        Cinema cinema = new Cinema();
        cinema.setId(1L);

        Room existingRoom = new Room();
        existingRoom.setId(1L);
        existingRoom.setName("Sala 1");
        existingRoom.setCapacity(50);
        existingRoom.setSeatsPerRow(10);
        existingRoom.setCinema(cinema);

        Room roomFromMapper = new Room();
        roomFromMapper.setType(RoomType.STANDARD);

        RoomResponseDTO responseDTO = new RoomResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Sala Atualizada");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(cinemaRepository.findById(1L)).thenReturn(Optional.of(cinema));
        when(roomMapper.toEntity(dto)).thenReturn(roomFromMapper);
        when(roomRepository.save(any(Room.class))).thenReturn(existingRoom);
        when(roomMapper.toResponseDTO(existingRoom)).thenReturn(responseDTO);

        // ACT
        RoomResponseDTO result = roomService.update(1L, dto);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Sala Atualizada");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando sala não for encontrada ao atualizar")
    void shouldThrowExceptionWhenRoomNotFoundOnUpdate() {

        // ARRANGE
        RoomCreateRequestDTO dto = new RoomCreateRequestDTO();
        dto.setName("Sala Atualizada");
        dto.setCinemaId(1L);

        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> roomService.update(99L, dto));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando cinema não for encontrado ao atualizar")
    void shouldThrowExceptionWhenCinemaNotFoundOnUpdate() {

        // ARRANGE
        RoomCreateRequestDTO dto = new RoomCreateRequestDTO();
        dto.setName("Sala Atualizada");
        dto.setCapacity(50);
        dto.setSeatsPerRow(10);
        dto.setCinemaId(99L);
        dto.setType("STANDARD");

        Room existingRoom = new Room();
        existingRoom.setId(1L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(cinemaRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> roomService.update(1L, dto));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve deletar uma sala com sucesso")
    void shouldDeleteRoomSuccessfully() {

        // ARRANGE
        Room room = new Room();
        room.setId(1L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        // ACT
        roomService.delete(1L);

        // ASSERT
        verify(roomRepository).delete(room);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao deletar sala não encontrada")
    void shouldThrowExceptionWhenDeletingNonExistentRoom() {

        // ARRANGE
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT
        Throwable exception = catchThrowable(() -> roomService.delete(99L));

        // ASSERT
        assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}