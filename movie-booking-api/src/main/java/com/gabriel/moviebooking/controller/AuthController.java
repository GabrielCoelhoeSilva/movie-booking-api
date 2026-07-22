package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.controller.docs.AuthControllerDocs;
import com.gabriel.moviebooking.dto.auth.*;
import com.gabriel.moviebooking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        MessageResponseDTO response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/verify")
    public ResponseEntity<AuthResponseDTO> verify(@Valid @RequestBody VerifyCodeRequestDTO dto) {
        AuthResponseDTO response = authService.verify(dto);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/resend-code")
    public ResponseEntity<MessageResponseDTO> resendCode(@Valid @RequestBody ResendCodeRequestDTO dto) {
        MessageResponseDTO response = authService.resendCode(dto);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        return ResponseEntity.ok(authService.forgotPassword(dto));
    }

    @Override
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        return ResponseEntity.ok(authService.resetPassword(dto));
    }
}