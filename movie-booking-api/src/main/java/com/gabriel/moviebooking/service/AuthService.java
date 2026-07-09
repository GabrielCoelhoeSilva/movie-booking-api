package com.gabriel.moviebooking.service;
import com.gabriel.moviebooking.dto.auth.ForgotPasswordRequestDTO;
import com.gabriel.moviebooking.dto.auth.ResetPasswordRequestDTO;

import com.gabriel.moviebooking.dto.auth.*;

public interface AuthService {
    MessageResponseDTO register(RegisterRequestDTO dto);

    AuthResponseDTO login(LoginRequestDTO dto);

    AuthResponseDTO verify(VerifyCodeRequestDTO dto);

    MessageResponseDTO resendCode(ResendCodeRequestDTO dto);

    MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO dto);

    MessageResponseDTO resetPassword(ResetPasswordRequestDTO dto);
}