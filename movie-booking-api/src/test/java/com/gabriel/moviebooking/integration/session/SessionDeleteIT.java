package com.gabriel.moviebooking.integration.session;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SessionDeleteIT {

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
    void deveExcluirSessaoComSucessoQuandoUsuarioForAdmin() throws Exception {

        Session session = sessionRepository.save(SessionFactory.createSession(movie, room));

        mockMvc.perform(delete("/api/v1/sessions/{id}", session.getId()))
                .andExpect(status().isNoContent());

        assertThat(sessionRepository.existsById(session.getId())).isFalse();

        assertThat(sessionRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoExcluirSessaoInexistente() throws Exception {

        mockMvc.perform(delete("/api/v1/sessions/{id}", 999L))
                .andExpect(status().isNotFound());

        assertThat(sessionRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void deveRetornarForbiddenQuandoCustomerTentarExcluirSessao() throws Exception {

        Session session = sessionRepository.save(SessionFactory.createSession(movie, room));

        mockMvc.perform(delete("/api/v1/sessions/{id}", session.getId()))
                .andExpect(status().isForbidden());

        assertThat(sessionRepository.existsById(session.getId())).isTrue();
    }

    @Test
    void deveRetornarForbiddenAoExcluirSessaoSemAutenticacao() throws Exception {

        Session session = sessionRepository.save(SessionFactory.createSession(movie, room));

        mockMvc.perform(delete("/api/v1/sessions/{id}", session.getId()))
                .andExpect(status().isForbidden());

        assertThat(sessionRepository.existsById(session.getId()))
                .isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestQuandoIdForInvalido() throws Exception {

        mockMvc.perform(delete("/api/v1/sessions/abc"))
                .andExpect(status().isBadRequest());

        assertThat(sessionRepository.count())
                .isZero();
    }
}
