package com.aluracursos.forohub.security;

import com.aluracursos.forohub.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", ""); // Extrae el token quitando "Bearer "
            logger.info("SecurityFilter: Token encontrado en el encabezado: {}", token.substring(0, Math.min(20, token.length())) + "...");

            try {
                // 1. Valida el token y obtiene el email del usuario
                String emailUsuario = tokenService.validarToken(token);
                logger.info("SecurityFilter: Token válido. Email extraído: {}", emailUsuario);

                // 2. Carga el usuario desde la base de datos usando el email obtenido del token
                UserDetails usuario = usuarioRepository.findByEmail(emailUsuario)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado en el filtro de seguridad"));
                logger.info("SecurityFilter: Usuario encontrado en la base de datos: {}", usuario.getUsername());

                // 3. Si es válido, configura la autenticación en el contexto de Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null, // No credentials necesarias aquí
                        usuario.getAuthorities() // Usa los roles/authorities del usuario si los tienes
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("SecurityFilter: Autenticación configurada en el contexto de seguridad para el usuario: {}", usuario.getUsername());

                logger.info("SecurityFilter: Sobre a llamar a filterChain.doFilter(). Autenticación debería estar lista.");

            } catch (RuntimeException e) {
                logger.warn("SecurityFilter: Error al validar token o cargar usuario: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido o expirado");

                return;

            }

        } else {
            logger.info("SecurityFilter: No se encontró encabezado Authorization con prefijo Bearer. Continuando sin autenticación.");
        }


        logger.info("SecurityFilter: Continuando la cadena de filtros.");

        // 4. Continúa la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
