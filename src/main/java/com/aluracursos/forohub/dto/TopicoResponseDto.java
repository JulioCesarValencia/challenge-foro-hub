package com.aluracursos.forohub.dto;

import java.time.LocalDateTime;

public record TopicoResponseDto(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        String status,
        Long autorId,
        String curso

) {
}
