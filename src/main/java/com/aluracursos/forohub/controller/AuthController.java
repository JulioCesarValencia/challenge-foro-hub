package com.aluracursos.forohub.controller;

import com.aluracursos.forohub.dto.AuthenticationRequestDto;
import com.aluracursos.forohub.dto.AuthenticationResponseDto;
import com.aluracursos.forohub.dto.RegisterUserDto;
import com.aluracursos.forohub.service.AuthenticationService;
import com.aluracursos.forohub.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RegistrationService registrationService; // Inyectamos el nuevo servicio

    @PostMapping("/login") // Endpoint para login: POST /auth/login
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody @Valid AuthenticationRequestDto request) {
        try {
            AuthenticationResponseDto response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PostMapping("/register") // Nuevo endpoint para registro: POST /auth/register
    public ResponseEntity<String> register(@RequestBody @Valid RegisterUserDto registerDto) {
        try {
            // Llama al servicio de registro
            registrationService.registerUser(registerDto);
            // Devuelve una respuesta de éxito
            return ResponseEntity.ok("Usuario registrado exitosamente.");
        } catch (RuntimeException e) {
            // Maneja el error de usuario duplicado (u otros) devolviendo un 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
