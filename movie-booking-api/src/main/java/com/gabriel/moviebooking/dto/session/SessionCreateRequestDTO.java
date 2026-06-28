package com.gabriel.moviebooking.dto.session;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreateRequestDTO {

    @NotNull(message = "O ID do filme é obrigatório.")
    private Long movieId;

    @NotNull(message = "O ID da sala é obrigatório.")
    private Long roomId;

    @NotNull(message = "O horário de início é obrigatório.")
    @Future(message = "O horário de início deve ser no futuro.")
    private LocalDateTime startTime;

    @NotNull(message = "O preço é obrigatório.")
    private BigDecimal price;
}