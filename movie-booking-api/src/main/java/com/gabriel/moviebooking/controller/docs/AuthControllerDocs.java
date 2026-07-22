package com.gabriel.moviebooking.controller.docs;

import com.gabriel.moviebooking.dto.auth.*;
import com.gabriel.moviebooking.exception.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public interface AuthControllerDocs {

    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria uma nova conta com role CUSTOMER e envia um código de ativação por e-mail."
    )
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Email já cadastrado ou dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto);

    @Operation(
            summary = "Login",
            description = "Autentica o usuário com email e senha e retorna um token JWT"
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Email ou senha inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto);

    @Operation(summary = "Verificar e-mail", description = "Valida o código enviado por e-mail e ativa a conta do usuário.")
    @ApiResponse(responseCode = "200", description = "Conta verificada com sucesso",
            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Código inválido ou expirado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<AuthResponseDTO> verify(@Valid @RequestBody VerifyCodeRequestDTO dto);

    @Operation(summary = "Reenviar código", description = "Gera e envia um novo código de ativação para o e-mail informado.")
    @ApiResponse(responseCode = "200", description = "Código reenviado com sucesso")
    @ApiResponse(responseCode = "400", description = "E-mail não encontrado ou já verificado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<MessageResponseDTO> resendCode(@Valid @RequestBody ResendCodeRequestDTO dto);

    @Operation(summary = "Esqueci minha senha", description = "Envia um código de redefinição de senha para o e-mail informado.")
    @ApiResponse(responseCode = "200", description = "Código de redefinição enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "E-mail não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto);

    @Operation(summary = "Redefinir senha", description = "Redefine a senha do usuário a partir do código de redefinição enviado por e-mail.")
    @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso")
    @ApiResponse(responseCode = "400", description = "Código inválido, expirado ou senha não atende aos critérios",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    ResponseEntity<MessageResponseDTO> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto);
}