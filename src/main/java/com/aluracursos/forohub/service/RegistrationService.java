package com.aluracursos.forohub.service;

import com.aluracursos.forohub.dto.RegisterUserDto;
import com.aluracursos.forohub.model.Usuario;
import com.aluracursos.forohub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(RegisterUserDto registerDto) {

        if (usuarioRepository.findByEmail(registerDto.email()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario registrado con el email: " + registerDto.email());
        }

        // 2. Hashear la contraseña recibida
        String passwordHasheada = passwordEncoder.encode(registerDto.password());

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registerDto.nombre());
        nuevoUsuario.setEmail(registerDto.email());
        nuevoUsuario.setPassword(passwordHasheada); // <-- Guardamos la contraseña hasheada

        // 4. Guardar el nuevo usuario en la base de datos
        usuarioRepository.save(nuevoUsuario);
    }

}
