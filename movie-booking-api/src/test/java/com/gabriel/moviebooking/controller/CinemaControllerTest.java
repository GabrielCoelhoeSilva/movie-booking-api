package com.gabriel.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaResponseDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
import com.gabriel.moviebooking.enums.State;
import com.gabriel.moviebooking.exception.CinemaNotFoundException;
import com.gabriel.moviebooking.security.JwtAuthenticationFilter;
import com.gabriel.moviebooking.security.JwtService;
import com.gabriel.moviebooking.security.RateLimitingFilter;
import com.gabriel.moviebooking.security.SecurityConfig;
import com.gabriel.moviebooking.service.CinemaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

@WebMvcTest(CinemaController.class)
@Import(SecurityConfig.class)
class CinemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CinemaService cinemaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private RateLimitingFilter rateLimitingFilter;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CinemaCreateDTO createDTO;
    private CinemaUpdateDTO updateDTO;
    private CinemaResponseDTO responseDTO;

    @BeforeEach
    void setUp() throws Exception {

        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(rateLimitingFilter).doFilter(any(), any(), any());

        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());


        createDTO = new CinemaCreateDTO();
        createDTO.setName("CineStar Paulista");
        createDTO.setCnpj("12345678000190");
        createDTO.setEmail("contato@cinestar.com.br");
        createDTO.setPhone("11987654321");
        createDTO.setStreet("Avenida Paulista");
        createDTO.setNumber("1500");
        createDTO.setComplement("Shopping Paulista, 3 andar");
        createDTO.setDistrict("Bela Vista");
        createDTO.setCity("Sao Paulo");
        createDTO.setState(State.SP);
        createDTO.setZipCode("01310100");

        updateDTO = new CinemaUpdateDTO();
        updateDTO.setName("CineStar Paulista Atualizado");
        updateDTO.setEmail("novoemail@cinestar.com.br");
        updateDTO.setPhone("11999999999");
        updateDTO.setStreet("Avenida Paulista");
        updateDTO.setNumber("2000");
        updateDTO.setDistrict("Bela Vista");
        updateDTO.setCity("Sao Paulo");
        updateDTO.setState(State.SP);
        updateDTO.setZipCode("01310100");

        responseDTO = new CinemaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("CineStar Paulista");
        responseDTO.setCnpj("12345678000190");
        responseDTO.setEmail("contato@cinestar.com.br");
        responseDTO.setPhone("11987654321");
        responseDTO.setStreet("Avenida Paulista");
        responseDTO.setNumber("1500");
        responseDTO.setDistrict("Bela Vista");
        responseDTO.setCity("Sao Paulo");
        responseDTO.setState(State.SP);
        responseDTO.setZipCode("01310100");
    }


    @Test
    @DisplayName("Deve criar cinema com sucesso como ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateCinemaSuccessfullyAsAdmin() throws Exception {

        when(cinemaService.create(any(CinemaCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/cinemas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("CineStar Paulista"))
                .andExpect(jsonPath("$.email").value("contato@cinestar.com.br"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando CUSTOMER tenta criar cinema")
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturn403WhenCustomerTriesToCreateCinema() throws Exception {

        mockMvc.perform(post("/api/v1/cinemas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 400 quando body invalido ao criar cinema")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenInvalidBodyOnCreate() throws Exception {

        CinemaCreateDTO invalidDTO = new CinemaCreateDTO();


        mockMvc.perform(post("/api/v1/cinemas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }



    @Test
    @DisplayName("Deve retornar lista de cinemas com sucesso")
    void shouldReturnAllCinemas() throws Exception {

        CinemaResponseDTO responseDTO2 = new CinemaResponseDTO();
        responseDTO2.setId(2L);
        responseDTO2.setName("Cinemax Boulevard");

        when(cinemaService.findAll()).thenReturn(List.of(responseDTO, responseDTO2));

        mockMvc.perform(get("/api/v1/cinemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("CineStar Paulista"))
                .andExpect(jsonPath("$[1].name").value("Cinemax Boulevard"));
    }


    @Test
    @DisplayName("Deve retornar cinema quando encontrado por ID")
    void shouldReturnCinemaWhenFoundById() throws Exception {

        when(cinemaService.findById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/cinemas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("CineStar Paulista"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando cinema nao encontrado por ID")
    void shouldReturn404WhenCinemaNotFound() throws Exception {

        when(cinemaService.findById(99L))
                .thenThrow(new CinemaNotFoundException(99L));

        mockMvc.perform(get("/api/v1/cinemas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Deve atualizar cinema com sucesso como ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateCinemaSuccessfullyAsAdmin() throws Exception {

        CinemaResponseDTO updatedResponseDTO = new CinemaResponseDTO();
        updatedResponseDTO.setId(1L);
        updatedResponseDTO.setName("CineStar Paulista Atualizado");

        when(cinemaService.update(eq(1L), any(CinemaUpdateDTO.class))).thenReturn(updatedResponseDTO);

        mockMvc.perform(put("/api/v1/cinemas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("CineStar Paulista Atualizado"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando CUSTOMER tenta atualizar cinema")
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturn403WhenCustomerTriesToUpdateCinema() throws Exception {

        mockMvc.perform(put("/api/v1/cinemas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 quando cinema nao encontrado ao atualizar")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenCinemaNotFoundOnUpdate() throws Exception {

        when(cinemaService.update(eq(99L), any(CinemaUpdateDTO.class)))
                .thenThrow(new CinemaNotFoundException(99L));

        mockMvc.perform(put("/api/v1/cinemas/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }


    @Test
    @DisplayName("Deve deletar cinema com sucesso como ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteCinemaSuccessfullyAsAdmin() throws Exception {

        mockMvc.perform(delete("/api/v1/cinemas/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 403 quando CUSTOMER tenta deletar cinema")
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturn403WhenCustomerTriesToDeleteCinema() throws Exception {

        mockMvc.perform(delete("/api/v1/cinemas/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 quando cinema nao encontrado ao deletar")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenCinemaNotFoundOnDelete() throws Exception {

        doThrow(new CinemaNotFoundException(99L))
                .when(cinemaService).delete(99L);

        mockMvc.perform(delete("/api/v1/cinemas/99")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}