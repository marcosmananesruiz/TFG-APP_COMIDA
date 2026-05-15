package org.dam2.bomboplatsserver.config;

import org.dam2.bomboplatsserver.modelo.mapper.*;
import org.dam2.bomboplatsserver.repo.RestauranteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración general del servidor.
 * Registra como beans de Spring todos los mappers de entidades,
 * haciéndolos disponibles para inyección en el resto de la aplicación.
 */
@Configuration
public class ServerConfig {

    @Bean
    public UserEntityMapper userEntityMapper() {
        return new UserEntityMapper();
    }

    @Bean
    public DireccionEntityMapper direccionEntityMapper() {
        return new DireccionEntityMapper();
    }

    @Bean
    public PedidoEntityMapper pedidoEntityMapper() {
        return new PedidoEntityMapper();
    }

    @Bean
    public PlatoEntityMapper platoEntityMapper() {
        return new PlatoEntityMapper();
    }

    @Bean
    public RestauranteEntityMapper restauranteEntityMapper() {
        return new RestauranteEntityMapper();
    }


}
