package com.aluracursos.forohub.security;

import com.aluracursos.forohub.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private static final String SECRET_KEY = "Mi clave sereta para forohub";
    private static final String ISSUER = "forohub-api";

    public String generarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withClaim("nombre", usuario.getNombre())
                    .withExpiresAt(generarFechaExpiracion())
                    .sign(algorithm);

            return token;
        } catch(JWTCreationException exception) {
                // Excepción lanzada si ocurre algún problema al crear el token
                throw new RuntimeException("Error al generar el token JWT", exception);
            }
        }

        public String validarToken(String token) {
            try {
                Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
                // Verifica el token: firma, expiración, issuer (si lo pones)
                DecodedJWT decodedJWT = JWT.require(algorithm)
                        .withIssuer(ISSUER)
                        .build()
                        .verify(token);

                // Devuelve el 'subject', que debería ser el email del usuario
                return decodedJWT.getSubject();

            } catch (JWTVerificationException exception) {
                // Excepción lanzada si el token es inválido o ha expirado
                throw new RuntimeException("Token JWT inválido o expirado", exception);
            }
        }

        // Genera una fecha de expiración (por ejemplo, 2 horas desde ahora)
        private Instant generarFechaExpiracion() {
            return LocalDateTime.now()
                    .plusDays(1) // expira en un dia solo mientras construyo la app
                    //.plusHours(2) // El token expira en 2 horas
                    .toInstant(ZoneOffset.of("-05:00"));
        }
}

