package com.aluracursos.forohub.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicoRequestDto(
        @NotBlank(message = "El título no puede estar en blanco.")
        String titulo,

        @NotBlank(message = "El mensaje no puede estar en blanco.")
        String mensaje,

        @NotBlank(message = "El curso es obligatorio.")
        String curso
) {
}
