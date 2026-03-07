package com.aluracursos.forohub.controller;

import com.aluracursos.forohub.dto.TopicoRequestDto;
import com.aluracursos.forohub.dto.TopicoResponseDto;
import com.aluracursos.forohub.model.Topico;
import com.aluracursos.forohub.model.Usuario;
import com.aluracursos.forohub.repository.TopicoRepository;
import com.aluracursos.forohub.repository.UsuarioRepository;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/topicos")
public class TopicoController {

    private static final Logger logger = LoggerFactory.getLogger(TopicoController.class);

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> crearTopico(@Valid @RequestBody TopicoRequestDto topicoRequestDto, Authentication authentication) {
        {
            //solo para pruebas hay que cambiar a jwt

            logger.info("TopicoController: Iniciando creación de tópico. Usuario autenticado: {}", authentication.getName());


            Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();

            // Verificar duplicado
            if (topicoRepository.existsByTituloAndMensaje(topicoRequestDto.titulo(), topicoRequestDto.mensaje())) {
                return ResponseEntity.badRequest().body("Ya existe un tópico con el mismo título y mensaje.");
            }

            // Crear la entidad Topico a partir del DTO y el usuario encontrado
            Topico topicoToSave = new Topico(
                    topicoRequestDto.titulo(),
                    topicoRequestDto.mensaje(),
                    java.time.LocalDateTime.now(),
                    "NO_RESPONDIDO",
                    usuarioAutenticado,
                    topicoRequestDto.curso()
            );


            Topico topicoGuardado = topicoRepository.save(topicoToSave);

            // Crear el DTO de respuesta
            TopicoResponseDto responseDto = new TopicoResponseDto(
                    topicoGuardado.getId(),
                    topicoGuardado.getTitulo(),
                    topicoGuardado.getMensaje(),
                    topicoGuardado.getFechaCreacion(),
                    topicoGuardado.getStatus(),
                    topicoGuardado.getAutor().getId(), // Devolver el ID del autor
                    topicoGuardado.getCurso()
            );

            // Devolver 201 Created con el DTO de respuesta
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }


    }
}
