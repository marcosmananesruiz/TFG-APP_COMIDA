package org.dam2.bomboplatsserver.config;

import org.dam2.bomboplatsserver.modelo.mapper.DireccionEntityMapper;
import org.dam2.bomboplatsserver.modelo.mapper.UserEntityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
