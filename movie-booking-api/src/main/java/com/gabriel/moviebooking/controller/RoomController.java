package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.controller.docs.RoomControllerDocs;
import com.gabriel.moviebooking.dto.room.RoomCreateRequestDTO;
import com.gabriel.moviebooking.dto.room.RoomResponseDTO;
import com.gabriel.moviebooking.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController implements RoomControllerDocs {

    private final RoomService roomService;

    @Override
    @PostMapping
    public ResponseEntity<RoomResponseDTO> create(@Valid @RequestBody RoomCreateRequestDTO dto) {
        log.info("Tentativa de cadastro de nova sala: '{}' para o Cinema ID: {}", dto.getName(), dto.getCinemaId());

        RoomResponseDTO createdRoom = roomService.create(dto);

        log.info("Sala '{}' cadastrada com sucesso. ID gerado: {}", createdRoom.getName(), createdRoom.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> update(@PathVariable Long id,
                                                  @Valid @RequestBody RoomCreateRequestDTO dto) {
        log.info("Solicitada atualização para a sala ID: {}. Novos dados - Nome: '{}'", id, dto.getName());

        RoomResponseDTO updatedRoom = roomService.update(id, dto);

        log.info("Sala ID: {} atualizada com sucesso.", id);

        return ResponseEntity.ok(updatedRoom);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> findById(@PathVariable Long id) {
        log.info("Buscando dados da sala ID: {}", id);

        RoomResponseDTO room = roomService.findById(id);

        return ResponseEntity.ok(room);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> findAll() {
        log.info("Listando todas as salas cadastradas no sistema");

        List<RoomResponseDTO> rooms = roomService.findAll();

        log.info("Total de salas listadas: {}", rooms.size());

        return ResponseEntity.ok(rooms);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Solicitada exclusão da sala ID: {}", id);

        roomService.delete(id);

        log.info("Sala ID: {} excluída com sucesso do sistema", id);

        return ResponseEntity.noContent().build();
    }
}