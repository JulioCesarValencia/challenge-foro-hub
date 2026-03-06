package com.aluracursos.forohub.service;

import com.aluracursos.forohub.dto.AuthenticationRequestDto;
import com.aluracursos.forohub.dto.AuthenticationResponseDto;
import com.aluracursos.forohub.model.Usuario;
import com.aluracursos.forohub.repository.UsuarioRepository;
import com.aluracursos.forohub.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + request.email()));

        String jwt = tokenService.generarToken(usuario);

        return new AuthenticationResponseDto(jwt);

    }
}
