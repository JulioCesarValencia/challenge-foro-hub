package com.aluracursos.forohub.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDto(
        @NotBlank(message = "El email es obligatorio.")
        String email,
        @NotBlank(message = "La contraseña es obligatoria.")
        String password
) {
}
