package com.gabriel.moviebooking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.moviebooking.dto.cinema.CinemaCreateDTO;
import com.gabriel.moviebooking.dto.cinema.CinemaUpdateDTO;
import com.gabriel.moviebooking.entity.Cinema;
import com.gabriel.moviebooking.factories.CinemaFactory;
import com.gabriel.moviebooking.repository.CinemaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CinemaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CinemaRepository cinemaRepository;

    //Endpoint: GET /api/v1/cinemas/
    @Test
    void deveRetornarTodosOsCinemas() throws Exception {

        cinemaRepository.save(CinemaFactory.createCinema());

        cinemaRepository.save(CinemaFactory.createSecondCinema());

        mockMvc.perform(get("/api/v1/cinemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].cnpj").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].phone").exists())
                .andExpect(jsonPath("$[0].street").exists())
                .andExpect(jsonPath("$[0].number").exists())
                .andExpect(jsonPath("$[0].district").exists())
                .andExpect(jsonPath("$[0].city").exists())
                .andExpect(jsonPath("$[0].state").exists())
                .andExpect(jsonPath("$[0].zipCode").exists());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremCinemas() throws Exception {

        cinemaRepository.deleteAll();

        mockMvc.perform(get("/api/v1/cinemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    //Endpoint: GET /api/v1/cinemas/{id}
    @Test
    void deveRetornarCinemaQuandoIdExistir() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        mockMvc.perform(get("/api/v1/cinemas/{id}", cinema.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cinema.getId()))
                .andExpect(jsonPath("$.name").value(cinema.getName()))
                .andExpect(jsonPath("$.cnpj").value(cinema.getCnpj()))
                .andExpect(jsonPath("$.email").value(cinema.getEmail()))
                .andExpect(jsonPath("$.phone").value(cinema.getPhone()))
                .andExpect(jsonPath("$.street").value(cinema.getStreet()))
                .andExpect(jsonPath("$.number").value(cinema.getNumber()))
                .andExpect(jsonPath("$.complement").value(cinema.getComplement()))
                .andExpect(jsonPath("$.district").value(cinema.getDistrict()))
                .andExpect(jsonPath("$.city").value(cinema.getCity()))
                .andExpect(jsonPath("$.state").value(cinema.getState().name()))
                .andExpect(jsonPath("$.zipCode").value(cinema.getZipCode()));
    }

    @Test
    void deveRetornarNotFoundQuandoCinemaNaoExistir() throws Exception {

        mockMvc.perform(get("/api/v1/cinemas/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarBadRequestQuandoIdForInvalido() throws Exception {

        mockMvc.perform(get("/api/v1/cinemas/abc"))
                .andExpect(status().isBadRequest());
    }

    //Endpoint: POST /api/v1/cinemas/
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarCinemaComSucessoQuandoUsuarioForAdmin() throws Exception {

        CinemaCreateDTO request = CinemaFactory.createRequestDTO();

        long cinemasBefore = cinemaRepository.count();

        mockMvc.perform(post("/api/v1/cinemas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.cnpj").value(request.getCnpj()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.phone").value(request.getPhone()))
                .andExpect(jsonPath("$.street").value(request.getStreet()))
                .andExpect(jsonPath("$.number").value(request.getNumber()))
                .andExpect(jsonPath("$.complement").value(request.getComplement()))
                .andExpect(jsonPath("$.district").value(request.getDistrict()))
                .andExpect(jsonPath("$.city").value(request.getCity()))
                .andExpect(jsonPath("$.state").value(request.getState().name()))
                .andExpect(jsonPath("$.zipCode").value(request.getZipCode()));

        assertThat(cinemaRepository.count()).isEqualTo(cinemasBefore + 1);

        Cinema cinema = cinemaRepository.findAll().stream()
                .filter(c -> c.getCnpj().equals(request.getCnpj()))
                .findFirst()
                .orElseThrow();

        assertThat(cinema.getName()).isEqualTo(request.getName());
        assertThat(cinema.getEmail()).isEqualTo(request.getEmail());
        assertThat(cinema.getPhone()).isEqualTo(request.getPhone());
        assertThat(cinema.getStreet()).isEqualTo(request.getStreet());
        assertThat(cinema.getNumber()).isEqualTo(request.getNumber());
        assertThat(cinema.getComplement()).isEqualTo(request.getComplement());
        assertThat(cinema.getDistrict()).isEqualTo(request.getDistrict());
        assertThat(cinema.getCity()).isEqualTo(request.getCity());
        assertThat(cinema.getState()).isEqualTo(request.getState());
        assertThat(cinema.getZipCode()).isEqualTo(request.getZipCode());
    }

    @Test
    void deveRetornarForbiddenQuandoUsuarioNaoEstiverAutenticado() throws Exception {

        CinemaCreateDTO request = CinemaFactory.createRequestDTO();

        mockMvc.perform(post("/api/v1/cinemas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        assertThat(cinemaRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "customer@test.com", roles = "CUSTOMER")
    void deveRetornarForbiddenQuandoUsuarioForCustomer() throws Exception {

        CinemaCreateDTO request = CinemaFactory.createRequestDTO();

        mockMvc.perform(post("/api/v1/cinemas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        assertThat(cinemaRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestQuandoDadosForemInvalidos() throws Exception {

        CinemaCreateDTO request = new CinemaCreateDTO();
        request.setName("");

        mockMvc.perform(post("/api/v1/cinemas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(cinemaRepository.count()).isZero();
    }

    //Endpoint: PUT /api/v1/cinemas/{id}
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAtualizarCinemaQuandoUsuarioForAdmin() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        CinemaUpdateDTO request = CinemaFactory.createUpdateDTO();

        mockMvc.perform(put("/api/v1/cinemas/{id}", cinema.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cinema.getId()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.phone").value(request.getPhone()))
                .andExpect(jsonPath("$.street").value(request.getStreet()))
                .andExpect(jsonPath("$.number").value(request.getNumber()))
                .andExpect(jsonPath("$.complement").value(request.getComplement()))
                .andExpect(jsonPath("$.district").value(request.getDistrict()))
                .andExpect(jsonPath("$.city").value(request.getCity()))
                .andExpect(jsonPath("$.state").value(request.getState().name()))
                .andExpect(jsonPath("$.zipCode").value(request.getZipCode()));

        Cinema updatedCinema = cinemaRepository.findById(cinema.getId()).orElseThrow();

        assertThat(updatedCinema.getName()).isEqualTo(request.getName());
        assertThat(updatedCinema.getEmail()).isEqualTo(request.getEmail());
        assertThat(updatedCinema.getPhone()).isEqualTo(request.getPhone());
        assertThat(updatedCinema.getStreet()).isEqualTo(request.getStreet());
        assertThat(updatedCinema.getNumber()).isEqualTo(request.getNumber());
        assertThat(updatedCinema.getComplement()).isEqualTo(request.getComplement());
        assertThat(updatedCinema.getDistrict()).isEqualTo(request.getDistrict());
        assertThat(updatedCinema.getCity()).isEqualTo(request.getCity());
        assertThat(updatedCinema.getState()).isEqualTo(request.getState());
        assertThat(updatedCinema.getZipCode()).isEqualTo(request.getZipCode());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoAtualizarQuandoCinemaNaoExistir() throws Exception {

        CinemaUpdateDTO request = CinemaFactory.createUpdateDTO();

        mockMvc.perform(put("/api/v1/cinemas/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deveRetornarForbiddenAoAtualizarCinemaQuandoUsuarioForCustomer() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        CinemaUpdateDTO request = CinemaFactory.createUpdateDTO();

        mockMvc.perform(put("/api/v1/cinemas/{id}", cinema.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornarForbiddenAoAtualizarCinemaQuandoUsuarioNaoEstiverAutenticado() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        CinemaUpdateDTO request = CinemaFactory.createUpdateDTO();

        mockMvc.perform(put("/api/v1/cinemas/{id}", cinema.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestAoAtualizarQuandoDadosForemInvalidos() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        CinemaUpdateDTO request = new CinemaUpdateDTO();
        request.setEmail("email-invalido");

        mockMvc.perform(put("/api/v1/cinemas/{id}", cinema.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    //Endpoint: DELETE /api/v1/cinemas/{id}
    @Test
    @WithMockUser(roles = "ADMIN")
    void deveExcluirCinemaComSucessoQuandoUsuarioForAdmin() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        mockMvc.perform(delete("/api/v1/cinemas/{id}", cinema.getId()))
                .andExpect(status().isNoContent());

        assertThat(cinemaRepository.existsById(cinema.getId())).isFalse();
        assertThat(cinemaRepository.count()).isZero();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarNotFoundAoExcluirCinemaInexistente() throws Exception {

        mockMvc.perform(delete("/api/v1/cinemas/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deveRetornarForbiddenQuandoCustomerTentarExcluirCinema() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        mockMvc.perform(delete("/api/v1/cinemas/{id}", cinema.getId()))
                .andExpect(status().isForbidden());

        assertThat(cinemaRepository.existsById(cinema.getId())).isTrue();
    }

    @Test
    void deveRetornarForbiddenAoExcluirCinemaSemAutenticacao() throws Exception {

        Cinema cinema = cinemaRepository.save(CinemaFactory.createCinema());

        mockMvc.perform(delete("/api/v1/cinemas/{id}", cinema.getId()))
                .andExpect(status().isForbidden());

        assertThat(cinemaRepository.existsById(cinema.getId())).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornarBadRequestQuandoExcluirCinemaEIdForInvalido() throws Exception {

        mockMvc.perform(delete("/api/v1/cinemas/abc"))
                .andExpect(status().isBadRequest());
    }

}
