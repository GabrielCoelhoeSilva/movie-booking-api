package com.gabriel.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.moviebooking.dto.movie.*;
import com.gabriel.moviebooking.enums.*;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.security.*;
import com.gabriel.moviebooking.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovieController.class)
@Import(SecurityConfig.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private MovieRequestDTO createRequestDTO() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Demon Slayer: Mugen Train");
        dto.setDescription("Tanjiro e seus amigos enfrentam demônios a bordo de um trem misterioso.");
        dto.setDuration(117);
        dto.setGenre(Genre.ACTION);
        dto.setAgeRating(AgeRating.FOURTEEN);
        return dto;
    }

    private MovieResponseDTO createResponseDTO(Long id, String title) {
        MovieResponseDTO dto = new MovieResponseDTO();
        dto.setId(id);
        dto.setTitle(title);
        return dto;
    }

    private MovieUpdateDTO createUpdateDTO() {
        MovieUpdateDTO dto = new MovieUpdateDTO();
        dto.setTitle("Jujutsu Kaisen 0");
        dto.setDescription("A história de Yuta Okkotsu e a maldição de sua amiga de infância.");
        dto.setDuration(105);
        dto.setGenre(Genre.ACTION);
        dto.setAgeRating(AgeRating.FOURTEEN);
        return dto;
    }

    @Test
    @DisplayName("Deve criar filme com sucesso como ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateMovieSuccessfullyAsAdmin() throws Exception {
        when(movieService.create(any())).thenReturn(createResponseDTO(1L, "Demon Slayer: Mugen Train"));

        mockMvc.perform(post("/api/v1/movies").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Demon Slayer: Mugen Train"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando CUSTOMER tenta criar filme")
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturn403WhenCustomerTriesToCreateMovie() throws Exception {
        mockMvc.perform(post("/api/v1/movies").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 400 quando body inválido ao criar filme")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenInvalidBodyOnCreate() throws Exception {
        mockMvc.perform(post("/api/v1/movies").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MovieRequestDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar filme quando encontrado por ID")
    void shouldReturnMovieWhenFoundById() throws Exception {
        when(movieService.findById(1L)).thenReturn(createResponseDTO(1L, "Demon Slayer: Mugen Train"));

        mockMvc.perform(get("/api/v1/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Demon Slayer: Mugen Train"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando filme não encontrado por ID")
    void shouldReturn404WhenMovieNotFound() throws Exception {
        when(movieService.findById(99L)).thenThrow(new ResourceNotFoundException("Movie not found with id: 99"));

        mockMvc.perform(get("/api/v1/movies/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Movie not found with id: 99"));
    }

    @Test
    @DisplayName("Deve retornar lista de filmes com sucesso")
    void shouldReturnAllMovies() throws Exception {
        when(movieService.findAll()).thenReturn(List.of(
                createResponseDTO(1L, "Demon Slayer: Mugen Train"),
                createResponseDTO(2L, "Jujutsu Kaisen 0")
        ));

        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Demon Slayer: Mugen Train"))
                .andExpect(jsonPath("$[1].title").value("Jujutsu Kaisen 0"));
    }

    @Test
    @DisplayName("Deve atualizar filme com sucesso como ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateMovieSuccessfullyAsAdmin() throws Exception {
        when(movieService.update(eq(1L), any())).thenReturn(createResponseDTO(1L, "Jujutsu Kaisen 0"));

        mockMvc.perform(put("/api/v1/movies/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Jujutsu Kaisen 0"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando CUSTOMER tenta atualizar filme")
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturn403WhenCustomerTriesToUpdateMovie() throws Exception {
        mockMvc.perform(put("/api/v1/movies/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 quando filme não encontrado ao atualizar")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenMovieNotFoundOnUpdate() throws Exception {
        when(movieService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("Movie not found with id: 99"));

        mockMvc.perform(put("/api/v1/movies/99").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateDTO())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Deve deletar filme com sucesso como ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteMovieSuccessfullyAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 403 quando CUSTOMER tenta deletar filme")
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturn403WhenCustomerTriesToDeleteMovie() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 quando filme não encontrado ao deletar")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteMovieNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Movie not found with id: 99")).when(movieService).delete(99L);

        mockMvc.perform(delete("/api/v1/movies/99").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}