package com.gabriel.moviebooking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.moviebooking.dto.movie.MovieRequestDTO;
import com.gabriel.moviebooking.dto.movie.MovieResponseDTO;
import com.gabriel.moviebooking.dto.movie.MovieUpdateDTO;
import com.gabriel.moviebooking.entity.Movie;
import com.gabriel.moviebooking.enums.AgeRating;
import com.gabriel.moviebooking.enums.Genre;
import com.gabriel.moviebooking.factories.MovieFactory;
import com.gabriel.moviebooking.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MovieControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;


    //Endpoint: POST /api/v1/movies
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarFilmeComSucessoQuandoUsuarioForAdmin() throws Exception {


        MovieRequestDTO request = MovieFactory.createRequestDTO();
        long moviesBefore = movieRepository.count();


        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.genre").value(request.getGenre().name()))
                .andExpect(jsonPath("$.ageRating").value(request.getAgeRating().name()));


        assertThat(movieRepository.count()).isEqualTo(moviesBefore + 1);

        Movie movie = movieRepository.findAll().stream()
                .filter(m -> m.getTitle().equals(request.getTitle()))
                .findFirst()
                .orElseThrow();

        assertThat(movie.getTitle()).isEqualTo(request.getTitle());
        assertThat(movie.getDescription()).isEqualTo(request.getDescription());
        assertThat(movie.getDuration()).isEqualTo(request.getDuration());
        assertThat(movie.getGenre()).isEqualTo(request.getGenre());
        assertThat(movie.getAgeRating()).isEqualTo(request.getAgeRating());
    }

    @Test
    void deveRetornarForbiddenQuandoUsuarioNaoEstiverAutenticado() throws Exception {

        MovieRequestDTO request = MovieFactory.createRequestDTO();

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void deveRetornarForbiddenQuandoUsuarioForCustomer() throws Exception {

        MovieRequestDTO request = MovieFactory.createRequestDTO();

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        assertThat(movieRepository.count()).isEqualTo(0);
    }

    //Endpoint: GET /api/v1/movies/{id}
    @Test
    void deveRetornarFilmeQuandoIdExistir() throws Exception {

        Movie movie = movieRepository.save(MovieFactory.createMovie());

        mockMvc.perform(get("/api/v1/movies/{id}", movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movie.getId()))
                .andExpect(jsonPath("$.title").value(movie.getTitle()))
                .andExpect(jsonPath("$.description").value(movie.getDescription()))
                .andExpect(jsonPath("$.genre").value(movie.getGenre().name()))
                .andExpect(jsonPath("$.ageRating").value(movie.getAgeRating().name()));
    }

    @Test
    void deveRetornarBadRequestQuandoIdForInvalido() throws Exception {

        mockMvc.perform(get("/api/v1/movies/abc"))
                .andExpect(status().isBadRequest());
    }

    //Endpoint: GET /api/v1/movies/
    @Test
    void deveRetornarTodosOsFilmes() throws Exception {


        movieRepository.save(MovieFactory.createMovie());

        movieRepository.save(MovieFactory.createSecondMovie());


        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].genre").exists())
                .andExpect(jsonPath("$[0].ageRating").exists());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremFilmes() throws Exception {

        movieRepository.deleteAll();

        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    //Endpoint: PUT /api/v1/movies/{id}
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarFilmeQuandoUsuarioForAdmin() throws Exception {

        Movie movie = movieRepository.save(MovieFactory.createMovie());

        MovieUpdateDTO request = MovieFactory.createUpdateDTO();

        mockMvc.perform(put("/api/v1/movies/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movie.getId()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.genre").value(request.getGenre().name()))
                .andExpect(jsonPath("$.ageRating").value(request.getAgeRating().name()));

        Movie updatedMovie = movieRepository.findById(movie.getId()).orElseThrow();

        assertThat(updatedMovie.getTitle()).isEqualTo(request.getTitle());
        assertThat(updatedMovie.getDescription()).isEqualTo(request.getDescription());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundQuandoFilmeNaoExistir() throws Exception {

        MovieUpdateDTO request = MovieFactory.createUpdateDTO();

        mockMvc.perform(put("/api/v1/movies/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deveRetornarForbiddenAoAtualizarFilmeQuandoUsuarioForCustomer() throws Exception {

        Movie movie = movieRepository.save(MovieFactory.createMovie());

        MovieUpdateDTO request = MovieFactory.createUpdateDTO();

        mockMvc.perform(put("/api/v1/movies/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornarForbiddenAoAtualizarFilmeQuandoUsuarioNaoEstiverAutenticado() throws Exception {

        Movie movie = movieRepository.save(MovieFactory.createMovie());

        MovieUpdateDTO request = MovieFactory.createUpdateDTO();

        mockMvc.perform(put("/api/v1/movies/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestQuandoDadosForemInvalidos() throws Exception {

        Movie movie = movieRepository.save(MovieFactory.createMovie());

        MovieUpdateDTO request = new MovieUpdateDTO();
        request.setTitle("");

        mockMvc.perform(put("/api/v1/movies/{id}", movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    //Endpoint: DELETE /api/v1/movies/{id}
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveExcluirFilmeComSucessoQuandoUsuarioForAdmin() throws Exception {

        Movie movie = movieRepository.save(MovieFactory.createMovie());

        mockMvc.perform(delete("/api/v1/movies/{id}", movie.getId()))
                .andExpect(status().isNoContent());

        assertThat(movieRepository.existsById(movie.getId())).isFalse();
        assertThat(movieRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoExcluirFilmeInexistente() throws Exception {

        mockMvc.perform(delete("/api/v1/movies/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deveRetornarForbiddenQuandoCustomerTentarExcluirFilme() throws Exception {

        Movie movie = movieRepository.save(MovieFactory.createMovie());

        mockMvc.perform(delete("/api/v1/movies/{id}", movie.getId()))
                .andExpect(status().isForbidden());

        assertThat(movieRepository.existsById(movie.getId())).isTrue();
    }

    @Test
    void deveRetornarForbiddenAoExcluirFilmeSemAutenticacao() throws Exception {

        Movie movie = movieRepository.save(MovieFactory.createMovie());

        mockMvc.perform(delete("/api/v1/movies/{id}", movie.getId()))
                .andExpect(status().isForbidden());

        assertThat(movieRepository.existsById(movie.getId())).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestQuandooExcluirFilmeeIdForInvalido() throws Exception {

        mockMvc.perform(delete("/api/v1/movies/abc"))
                .andExpect(status().isBadRequest());
    }

}
