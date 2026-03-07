package com.aluracursos.forohub.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserDto(
        @NotBlank(message = "El nombre es obligatorio.")
        String nombre,

        @NotBlank(message = "El email es obligatorio.")
        @Email(message = "El formato del email no es válido.")
        String email,

        @NotBlank(message = "La contraseña es obligatoria.")
        String password
) {
}
