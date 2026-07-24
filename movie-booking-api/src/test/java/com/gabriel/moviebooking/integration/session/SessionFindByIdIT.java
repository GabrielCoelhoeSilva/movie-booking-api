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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SessionFindByIdIT {

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
    void deveRetornarSessaoQuandoIdExistir() throws Exception {

        Session session = sessionRepository.save(
                SessionFactory.createSession(movie, room)
        );


        mockMvc.perform(get("/api/v1/sessions/{id}", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(session.getId()))
                .andExpect(jsonPath("$.movieId")
                        .value(movie.getId()))
                .andExpect(jsonPath("$.movieTitle")
                        .value(movie.getTitle()))
                .andExpect(jsonPath("$.roomId")
                        .value(room.getId()))
                .andExpect(jsonPath("$.roomName")
                        .value(room.getName()))
                .andExpect(jsonPath("$.startTime")
                        .exists())
                .andExpect(jsonPath("$.endTime")
                        .exists())
                .andExpect(jsonPath("$.price")
                        .value(35.00));
    }

    @Test
    void deveRetornarNotFoundQuandoSessaoNaoExistir() throws Exception {

        mockMvc.perform(get("/api/v1/sessions/{id}", 999L))
                .andExpect(status().isNotFound());

    }

    @Test
    void deveRetornarBadRequestQuandoIdForInvalido() throws Exception {

        mockMvc.perform(get("/api/v1/sessions/abc"))
                .andExpect(status().isBadRequest());
    }
}
