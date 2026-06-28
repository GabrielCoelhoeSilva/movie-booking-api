package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.auth.AuthResponseDTO;
import com.gabriel.moviebooking.dto.auth.LoginRequestDTO;
import com.gabriel.moviebooking.dto.auth.RegisterRequestDTO;
import com.gabriel.moviebooking.entity.User;
import com.gabriel.moviebooking.enums.Role;
import com.gabriel.moviebooking.exception.BusinessException;
import com.gabriel.moviebooking.repository.UserRepository;
import com.gabriel.moviebooking.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("Email already in use");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDTO(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}