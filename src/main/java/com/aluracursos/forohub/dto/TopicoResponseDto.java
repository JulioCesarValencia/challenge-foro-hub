package com.aluracursos.forohub.dto;

import com.aluracursos.forohub.model.Topico;

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
    public TopicoResponseDto(Topico topico) {
        this(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getAutor().getId(),
                topico.getCurso()
        );
    }
}



