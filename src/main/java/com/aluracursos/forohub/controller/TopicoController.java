package com.aluracursos.forohub.controller;

import com.aluracursos.forohub.dto.TopicoRequestDto;
import com.aluracursos.forohub.dto.TopicoResponseDto;
import com.aluracursos.forohub.model.Topico;
import com.aluracursos.forohub.model.Usuario;
import com.aluracursos.forohub.repository.TopicoRepository;
import com.aluracursos.forohub.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<?> crearTopico(@Valid @RequestBody TopicoRequestDto topicoRequestDto,
                                         Authentication authentication) {


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

            // DTO de respuesta
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


    @GetMapping
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<Page<TopicoResponseDto>> listarTopicos(
            @RequestParam(required = false) String curso,
            @PageableDefault(
                    size = 10,
                    sort = "fechaCreacion",
                    direction = Sort.Direction.ASC
            ) Pageable paginacion
    ) {

        Page<Topico> paginaTopicos;


        if(curso !=null&&!curso.isBlank()) {
            paginaTopicos = topicoRepository.findByCurso(curso, paginacion);
        } else {
            paginaTopicos = topicoRepository.findAll(paginacion);
        }

        var respuesta = paginaTopicos.map(TopicoResponseDto::new);

        // Devolver respuesta con estado 200 OK
        return ResponseEntity.ok(respuesta);

    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<TopicoResponseDto> obtenerDetalleTopico(@PathVariable Long id) {

        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado con ID: " + id));

        TopicoResponseDto detalleTopico = new TopicoResponseDto(topico);

        return ResponseEntity.ok(detalleTopico);
    }


    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<TopicoResponseDto> actualizarTopico(
            @PathVariable Long id,
            @Valid @RequestBody TopicoRequestDto datosActualizar,
            Authentication authentication
    ) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);

        if (topicoOptional.isPresent()) {
            Topico topicoExistente = topicoOptional.get();

            if (topicoRepository.existsByTituloAndMensajeAndIdNot(datosActualizar.titulo(), datosActualizar.mensaje(), id)) {
                return ResponseEntity.badRequest().body(null);
            }

            topicoExistente.setTitulo(datosActualizar.titulo());
            topicoExistente.setMensaje(datosActualizar.mensaje());
            topicoExistente.setCurso(datosActualizar.curso());

            Topico topicoActualizado = topicoRepository.save(topicoExistente);

            TopicoResponseDto respuestaDto = new TopicoResponseDto(topicoActualizado);


            return ResponseEntity.ok(respuestaDto);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado con ID: " + id);
        }

    }


    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<?> eliminarTopico(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Optional<Topico> topicoOptional = topicoRepository.findById(id);


        if (topicoOptional.isPresent()) {
            Topico topicoExistente = topicoOptional.get();


            topicoRepository.deleteById(id);


            return ResponseEntity.noContent().build();

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico no encontrado con ID: " + id);
        }
    }


}



