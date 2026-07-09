package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.auth.*;
import com.gabriel.moviebooking.entity.User;
import com.gabriel.moviebooking.enums.Role;
import com.gabriel.moviebooking.exception.BusinessException;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.repository.UserRepository;
import com.gabriel.moviebooking.security.JwtService;
import com.gabriel.moviebooking.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    @Transactional
    public MessageResponseDTO register(RegisterRequestDTO dto) {
        log.info("Tentativa de registro para o email: {}", dto.getEmail());

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("Email already in use");
        }

        String verificationCode = generateVerificationCode();

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setVerified(false);
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        emailService.sendVerificationCode(dto.getEmail(), dto.getName(), verificationCode);

        log.info("Usuário registrado com sucesso: id={}, email={}", user.getId(), user.getEmail());

        return new MessageResponseDTO("Verifique seu email para ativar sua conta.");
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        log.info("Tentativa de login para o email: {}", dto.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        } catch (Exception ex) {
            log.warn("Falha de autenticação para o email: {} - Motivo: {}", dto.getEmail(), ex.getMessage());
            throw ex;
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!user.isVerified()) {
            throw new BusinessException("Email not verified. Please check your inbox.");
        }

        String token = jwtService.generateToken(user);

        log.info("Login bem-sucedido: id={}, email={}", user.getId(), user.getEmail());

        return new AuthResponseDTO(token);
    }

    @Override
    @Transactional
    public AuthResponseDTO verify(VerifyCodeRequestDTO dto) {
        log.info("Tentativa de verificação de código para o email: {}", dto.getEmail());

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.getEmail()));

        if (user.isVerified()) {
            throw new BusinessException("Email already verified");
        }

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(dto.getCode())) {
            throw new BusinessException("Invalid verification code");
        }

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Verification code has expired");
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        log.info("Email verificado com sucesso para o usuário ID: {}", user.getId());
        return new AuthResponseDTO(token);
    }

    @Override
    @Transactional
    public MessageResponseDTO resendCode(ResendCodeRequestDTO dto) {
        log.info("Solicitado reenvio de código para o email: {}", dto.getEmail());

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.getEmail()));

        if (user.isVerified()) {
            throw new BusinessException("Email already verified");
        }

        String newCode = generateVerificationCode();
        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailService.sendVerificationCode(user.getEmail(), user.getName(), newCode);

        log.info("Novo código de verificação enviado para o email: {}", user.getEmail());
        return new MessageResponseDTO("Novo código enviado para " + dto.getEmail());
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}