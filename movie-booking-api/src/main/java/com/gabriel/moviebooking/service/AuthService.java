package com.gabriel.moviebooking.service;

import com.gabriel.moviebooking.dto.auth.*;

public interface AuthService {
    MessageResponseDTO register(RegisterRequestDTO dto);

    AuthResponseDTO login(LoginRequestDTO dto);

    AuthResponseDTO verify(VerifyCodeRequestDTO dto);

    MessageResponseDTO resendCode(ResendCodeRequestDTO dto);
}