package org.dam2.bomboplatsserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configuración de seguridad de la aplicación.
 * Define el cifrado de contraseñas, las reglas de acceso a los endpoints
 * y la política de CORS para permitir peticiones desde el cliente Angular.
 */
@Configuration
public class SecurityConfig {

    /**
     * Define el encoder de contraseñas usando BCrypt
     *
     * @return instancia de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Deshabilita CSRF (no necesario en APIs REST sin sesión)
     * y permite el acceso público a todos los endpoints.
     *
     * @param http objeto de configuración de seguridad reactiva
     * @return cadena de filtros de seguridad configurada
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .anyExchange().permitAll()
                )
                .build();
    }

    /**
     * Configura el filtro CORS para permitir peticiones desde el cliente Angular
     * en cualquier host en el puerto 4200.
     *
     * @return filtro CORS configurado
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://*:4200");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
