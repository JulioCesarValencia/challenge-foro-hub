package com.aluracursos.forohub.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class OpenApiConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        // Define el esquema de seguridad como HTTP Bearer Token
        SecurityScheme bearerTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // Tipo de autenticación: HTTP
                .scheme("bearer")              // Esquema: bearer
                .bearerFormat("JWT")           // Formato: JWT (esto es informativo)
                .in(SecurityScheme.In.HEADER)  // Donde se envía el token: en el encabezado HTTP
                .name("Authorization");        // Nombre del encabezado: Authorization

        // Crea el componente que contiene el esquema de seguridad
        Components components = new Components()
                .addSecuritySchemes("bearer-key", bearerTokenScheme);

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearer-key");


        // Crea la configuración OpenAPI y le asigna los componentes
        return new OpenAPI()
                .components(components) // Agrega los componentes (que incluyen el esquema de seguridad)
                //.addSecurityItem(new SecurityRequirement().addList("bearer-key"));
                .addSecurityItem(securityRequirement);
    }

}
