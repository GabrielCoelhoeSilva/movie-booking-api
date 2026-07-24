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
public class SessionFindAllIT {
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
    void deveRetornarTodasAsSessoes() throws Exception {

        Session session = sessionRepository.save(
                SessionFactory.createSession(movie, room)
        );


        mockMvc.perform(get("/api/v1/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(session.getId()))
                .andExpect(jsonPath("$[0].movieId")
                        .value(movie.getId()))
                .andExpect(jsonPath("$[0].movieTitle")
                        .value(movie.getTitle()))
                .andExpect(jsonPath("$[0].roomId")
                        .value(room.getId()))
                .andExpect(jsonPath("$[0].roomName")
                        .value(room.getName()))
                .andExpect(jsonPath("$[0].price")
                        .value(35.00));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremSessoes() throws Exception {

        sessionRepository.deleteAll();

        mockMvc.perform(get("/api/v1/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}
