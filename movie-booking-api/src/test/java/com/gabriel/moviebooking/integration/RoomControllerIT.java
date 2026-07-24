package com.gabriel.moviebooking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.moviebooking.dto.room.RoomCreateRequestDTO;
import com.gabriel.moviebooking.entity.Cinema;
import com.gabriel.moviebooking.entity.Room;
import com.gabriel.moviebooking.factories.CinemaFactory;
import com.gabriel.moviebooking.factories.RoomFactory;
import com.gabriel.moviebooking.repository.CinemaRepository;
import com.gabriel.moviebooking.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RoomControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CinemaRepository cinemaRepository;


    //Endpoint: POST /api/v1/rooms
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarSalaComSucessoQuandoUsuarioForAdmin() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        RoomCreateRequestDTO request = RoomFactory.createRequestDTO(cinema.getId());

        long roomsBefore = roomRepository.count();

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.capacity").value(request.getCapacity()))
                .andExpect(jsonPath("$.type").value(request.getType()))
                .andExpect(jsonPath("$.cinemaId").value(cinema.getId()))
                .andExpect(jsonPath("$.seatsPerRow").value(request.getSeatsPerRow()));

        assertThat(roomRepository.count()).isEqualTo(roomsBefore + 1);

        Room room = roomRepository.findAll().stream()
                .filter(r -> r.getName().equals(request.getName()))
                .findFirst()
                .orElseThrow();

        assertThat(room.getName()).isEqualTo(request.getName());
        assertThat(room.getCapacity()).isEqualTo(request.getCapacity());
        assertThat(room.getSeatsPerRow()).isEqualTo(request.getSeatsPerRow());
        assertThat(room.getCinema().getId()).isEqualTo(cinema.getId());
        assertThat(room.getType().name()).isEqualTo(request.getType());
    }

    @Test
    void deveRetornarForbiddenQuandoUsuarioNaoEstiverAutenticado() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        RoomCreateRequestDTO request = RoomFactory.createRequestDTO(cinema.getId());

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void deveRetornarForbiddenQuandoUsuarioForCustomer() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        RoomCreateRequestDTO request = RoomFactory.createRequestDTO(cinema.getId());

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        assertThat(roomRepository.count()).isEqualTo(0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestQuandoDadosForemInvalidos() throws Exception {

        RoomCreateRequestDTO request = new RoomCreateRequestDTO();
        request.setName("");
        request.setCapacity(5);
        request.setCinemaId(null);
        request.setType(null);
        request.setSeatsPerRow(0);

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(roomRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundQuandoCinemaNaoExistir() throws Exception {

        RoomCreateRequestDTO request = RoomFactory.createRequestDTO(999L);

        mockMvc.perform(post("/api/v1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assertThat(roomRepository.count()).isZero();
    }

    //Endpoint: PUT /api/v1/rooms/{id}
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarSalaQuandoUsuarioForAdmin() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());
        Cinema secondCinema = cinemaRepository.save(CinemaFactory.createSecondCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        RoomCreateRequestDTO request = RoomFactory.createUpdateRequestDTO(secondCinema.getId());

        mockMvc.perform(put("/api/v1/rooms/{id}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(room.getId()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.capacity").value(request.getCapacity()))
                .andExpect(jsonPath("$.type").value(request.getType()))
                .andExpect(jsonPath("$.cinemaId").value(secondCinema.getId()))
                .andExpect(jsonPath("$.seatsPerRow").value(request.getSeatsPerRow()));

        Room updatedRoom = roomRepository.findById(room.getId()).orElseThrow();

        assertThat(updatedRoom.getName()).isEqualTo(request.getName());
        assertThat(updatedRoom.getCapacity()).isEqualTo(request.getCapacity());
        assertThat(updatedRoom.getSeatsPerRow()).isEqualTo(request.getSeatsPerRow());
        assertThat(updatedRoom.getCinema().getId()).isEqualTo(secondCinema.getId());
        assertThat(updatedRoom.getType().name()).isEqualTo(request.getType());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundQuandoSalaNaoExistir() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        RoomCreateRequestDTO request = RoomFactory.createUpdateRequestDTO(cinema.getId());

        mockMvc.perform(put("/api/v1/rooms/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoAtualizarQuandoCinemaNaoExistir() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        RoomCreateRequestDTO request = RoomFactory.createUpdateRequestDTO(999L);

        mockMvc.perform(put("/api/v1/rooms/{id}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        Room persistedRoom = roomRepository.findById(room.getId()).orElseThrow();

        assertThat(persistedRoom.getCinema().getId()).isEqualTo(cinema.getId());
        assertThat(persistedRoom.getName()).isEqualTo(room.getName());
        assertThat(persistedRoom.getCapacity()).isEqualTo(room.getCapacity());
        assertThat(persistedRoom.getSeatsPerRow()).isEqualTo(room.getSeatsPerRow());
        assertThat(persistedRoom.getType()).isEqualTo(room.getType());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deveRetornarForbiddenAoAtualizarSalaQuandoUsuarioForCustomer() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        RoomCreateRequestDTO request = RoomFactory.createUpdateRequestDTO(cinema.getId());

        mockMvc.perform(put("/api/v1/rooms/{id}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        Room persistedRoom = roomRepository.findById(room.getId()).orElseThrow();

        assertThat(persistedRoom.getName()).isEqualTo(room.getName());
        assertThat(persistedRoom.getCapacity()).isEqualTo(room.getCapacity());
        assertThat(persistedRoom.getSeatsPerRow()).isEqualTo(room.getSeatsPerRow());
        assertThat(persistedRoom.getCinema().getId()).isEqualTo(cinema.getId());
        assertThat(persistedRoom.getType()).isEqualTo(room.getType());
    }

    @Test
    void deveRetornarForbiddenAoAtualizarSalaQuandoUsuarioNaoEstiverAutenticado() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        RoomCreateRequestDTO request = RoomFactory.createUpdateRequestDTO(cinema.getId());

        mockMvc.perform(put("/api/v1/rooms/{id}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        Room persistedRoom = roomRepository.findById(room.getId()).orElseThrow();

        assertThat(persistedRoom.getName()).isEqualTo(room.getName());
        assertThat(persistedRoom.getCapacity()).isEqualTo(room.getCapacity());
        assertThat(persistedRoom.getSeatsPerRow()).isEqualTo(room.getSeatsPerRow());
        assertThat(persistedRoom.getCinema().getId()).isEqualTo(cinema.getId());
        assertThat(persistedRoom.getType()).isEqualTo(room.getType());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestAoAtualizarQuandoDadosForemInvalidos() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        RoomCreateRequestDTO request = new RoomCreateRequestDTO();
        request.setName("");
        request.setCapacity(5);
        request.setCinemaId(cinema.getId());
        request.setType(null);
        request.setSeatsPerRow(0);

        mockMvc.perform(put("/api/v1/rooms/{id}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Room persistedRoom = roomRepository.findById(room.getId()).orElseThrow();

        assertThat(persistedRoom.getName()).isEqualTo(room.getName());
        assertThat(persistedRoom.getCapacity()).isEqualTo(room.getCapacity());
        assertThat(persistedRoom.getSeatsPerRow()).isEqualTo(room.getSeatsPerRow());
        assertThat(persistedRoom.getCinema().getId()).isEqualTo(cinema.getId());
        assertThat(persistedRoom.getType()).isEqualTo(room.getType());
    }

    //Endpoint: GET /api/v1/rooms/{id}
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarSalaQuandoUsuarioForAdmin() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        mockMvc.perform(get("/api/v1/rooms/{id}", room.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(room.getId()))
                .andExpect(jsonPath("$.name").value(room.getName()))
                .andExpect(jsonPath("$.capacity").value(room.getCapacity()))
                .andExpect(jsonPath("$.type").value(room.getType().name()))
                .andExpect(jsonPath("$.cinemaId").value(cinema.getId()))
                .andExpect(jsonPath("$.seatsPerRow").value(room.getSeatsPerRow()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoTentarObterSalaQuandoaSalaNaoExistir() throws Exception {

        mockMvc.perform(get("/api/v1/rooms/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deveRetornarForbiddenAoTentarObterSalaQuandoUsuarioForCustomer() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        mockMvc.perform(get("/api/v1/rooms/{id}", room.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornarForbiddenAoTentarObterSalaQuandoUsuarioNaoEstiverAutenticado() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        mockMvc.perform(get("/api/v1/rooms/{id}", room.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestQuandoIdForInvalido() throws Exception {

        mockMvc.perform(get("/api/v1/rooms/abc"))
                .andExpect(status().isBadRequest());
    }

    //Endpoint: GET /api/v1/rooms
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarTodasAsSalasQuandoUsuarioForAdmin() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        roomRepository.save(RoomFactory.createRoom(cinema));
        roomRepository.save(RoomFactory.createSecondRoom(cinema));

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].capacity").exists())
                .andExpect(jsonPath("$[0].type").exists())
                .andExpect(jsonPath("$[0].cinemaId").exists())
                .andExpect(jsonPath("$[0].seatsPerRow").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarListaVaziaQuandoNaoExistiremSalas() throws Exception {

        roomRepository.deleteAll();

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deveRetornarForbiddenAoTentarObterumaSalaQuandoUsuarioForCustomer() throws Exception {

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornarForbiddenQAoTentarObterumaSalauandoUsuarioNaoEstiverAutenticado() throws Exception {

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isForbidden());
    }

    //Endpoint: DELETE /api/v1/rooms
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveExcluirSalaComSucessoQuandoUsuarioForAdmin() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        mockMvc.perform(delete("/api/v1/rooms/{id}", room.getId()))
                .andExpect(status().isNoContent());

        assertThat(roomRepository.existsById(room.getId())).isFalse();
        assertThat(roomRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoExcluirSalaInexistente() throws Exception {

        mockMvc.perform(delete("/api/v1/rooms/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deveRetornarForbiddenQuandoCustomerTentarExcluirSala() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        mockMvc.perform(delete("/api/v1/rooms/{id}", room.getId()))
                .andExpect(status().isForbidden());

        assertThat(roomRepository.existsById(room.getId())).isTrue();
        assertThat(roomRepository.count()).isEqualTo(1);
    }

    @Test
    void deveRetornarForbiddenAoExcluirSalaSemAutenticacao() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        Room room = roomRepository.save(RoomFactory.createRoom(cinema));

        mockMvc.perform(delete("/api/v1/rooms/{id}", room.getId()))
                .andExpect(status().isForbidden());

        assertThat(roomRepository.existsById(room.getId())).isTrue();
        assertThat(roomRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestAoExcluirSalaQuandoIdForInvalido() throws Exception {

        mockMvc.perform(delete("/api/v1/rooms/abc"))
                .andExpect(status().isBadRequest());
    }

}
