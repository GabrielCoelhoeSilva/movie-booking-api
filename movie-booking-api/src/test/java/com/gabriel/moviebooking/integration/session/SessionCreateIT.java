package com.gabriel.moviebooking.integration.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.moviebooking.dto.session.SessionCreateRequestDTO;
import com.gabriel.moviebooking.entity.Cinema;
import com.gabriel.moviebooking.entity.Movie;
import com.gabriel.moviebooking.entity.Room;
import com.gabriel.moviebooking.entity.Session;
import com.gabriel.moviebooking.factories.CinemaFactory;
import com.gabriel.moviebooking.factories.MovieFactory;
import com.gabriel.moviebooking.factories.RoomFactory;
import com.gabriel.moviebooking.factories.SessionFactory;
import com.gabriel.moviebooking.repository.CinemaRepository;
import com.gabriel.moviebooking.repository.MovieRepository;
import com.gabriel.moviebooking.repository.RoomRepository;
import com.gabriel.moviebooking.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SessionCreateIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SessionRepository sessionRepository;

    Movie movie;
    Room room;

    @BeforeEach
    void setUp() {
        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        room = roomRepository.save(RoomFactory.createRoom(cinema));

        movie = movieRepository.save(MovieFactory.createMovie());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarSessaoComSucessoQuandoUsuarioForAdmin() throws Exception {

        SessionCreateRequestDTO request = SessionFactory.createRequestDTO(movie.getId(), room.getId());

        long sessoesAntes = sessionRepository.count();

        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.movieId")
                        .value(movie.getId()))
                .andExpect(jsonPath("$.movieTitle")
                        .value(movie.getTitle()))
                .andExpect(jsonPath("$.roomId")
                        .value(room.getId()))
                .andExpect(jsonPath("$.roomName")
                        .value(room.getName()))
                .andExpect(jsonPath("$.price")
                        .value(35.00));


        assertThat(sessionRepository.count()).isEqualTo(sessoesAntes + 1);

        Session session = sessionRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        assertThat(session.getMovie().getId()).isEqualTo(movie.getId());
        assertThat(session.getRoom().getId()).isEqualTo(room.getId());
        assertThat(session.getSeats()).isNotEmpty();
    }

    @Test
    void deveRetornarForbiddenQuandoUsuarioNaoEstiverAutenticado() throws Exception {

        SessionCreateRequestDTO request = SessionFactory.createRequestDTO(movie.getId(), room.getId());

        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());


        assertThat(sessionRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void deveRetornarForbiddenQuandoUsuarioForCustomer() throws Exception {

        SessionCreateRequestDTO request = SessionFactory.createRequestDTO(movie.getId(), room.getId());

        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());


        assertThat(sessionRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestQuandoDadosForemInvalidos() throws Exception {

        SessionCreateRequestDTO request = new SessionCreateRequestDTO();


        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());


        assertThat(sessionRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundQuandoFilmeNaoExistir() throws Exception {

        SessionCreateRequestDTO request = SessionFactory.createRequestDTO(999L, room.getId());

        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());


        assertThat(sessionRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundQuandoSalaNaoExistir() throws Exception {

        SessionCreateRequestDTO request = SessionFactory.createRequestDTO(movie.getId(), 999L);

        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());


        assertThat(sessionRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarConflictQuandoSalaJaPossuirSessaoNoMesmoHorario() throws Exception {

        Session sessaoExistente = SessionFactory.createSession(movie, room);

        sessionRepository.save(sessaoExistente);

        SessionCreateRequestDTO request = SessionFactory.createRequestDTO(movie.getId(), room.getId());

        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());


        assertThat(sessionRepository.count()).isEqualTo(1);
    }

}
