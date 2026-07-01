package com.gabriel.moviebooking.controller;

import com.gabriel.moviebooking.dto.auth.AuthResponseDTO;
import com.gabriel.moviebooking.dto.auth.LoginRequestDTO;
import com.gabriel.moviebooking.dto.auth.RegisterRequestDTO;
import com.gabriel.moviebooking.entity.User;
import com.gabriel.moviebooking.enums.Role;
import com.gabriel.moviebooking.exception.BusinessException;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import com.gabriel.moviebooking.repository.UserRepository;
import com.gabriel.moviebooking.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria uma nova conta com role CUSTOMER e retorna um token JWT"
    )
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Email já cadastrado ou dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
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
    @Operation(
            summary = "Login",
            description = "Autentica o usuário com email e senha e retorna um token JWT"
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Email ou senha inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}