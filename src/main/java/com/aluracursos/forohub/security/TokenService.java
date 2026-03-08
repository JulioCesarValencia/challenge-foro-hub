package com.aluracursos.forohub.security;

import com.aluracursos.forohub.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.expiration-in-minutes}")
    private Long expirationInMinutes; // Duración en minutos

    public String generarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            String token = JWT.create()
                    .withIssuer(issuer)
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
                Algorithm algorithm = Algorithm.HMAC256(secretKey);
                // Verifica el token: firma, expiración, issuer (si lo pones)
                DecodedJWT decodedJWT = JWT.require(algorithm)
                        .withIssuer(issuer)
                        .build()
                        .verify(token);

                return decodedJWT.getSubject();

            } catch (JWTVerificationException exception) {
                throw new RuntimeException("Token JWT inválido o expirado", exception);
            }
        }

        private Instant generarFechaExpiracion() {
            return LocalDateTime.now()
                    .plusMinutes(expirationInMinutes)
                    .toInstant(ZoneOffset.of("-05:00"));
        }
}

