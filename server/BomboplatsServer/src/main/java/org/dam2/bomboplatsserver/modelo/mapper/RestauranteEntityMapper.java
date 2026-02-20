package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Restaurante;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

@Component
public class RestauranteEntityMapper implements EntityMapper<RestauranteEntity, Restaurante> {

    @Override
    public Mono<RestauranteEntity> map(Mono<Restaurante> o) {
        return o.map(restaurante ->
                RestauranteEntity.builder()
                        .id(restaurante.getId())
                        .nombre(restaurante.getNombre())
                        .description(restaurante.getDescription())
                        .iconUrl(restaurante.getIconUrl())
                        .tags(
                                restaurante.getTags() != null
                                        ? restaurante.getTags().toArray(new String[0])
                                        : null
                        )
                        .build()
        );
    }

    @Override
    public Mono<Restaurante> unmap(Mono<RestauranteEntity> o) {
        return o.map(entity ->
                Restaurante.builder()
                        .id(entity.getId())
                        .nombre(entity.getNombre())
                        .description(entity.getDescription())
                        .iconUrl(entity.getIconUrl())
                        .tags(
                                entity.getTags() != null
                                        ? Arrays.asList(entity.getTags())
                                        : Collections.emptyList()
                        )
                        .platos(Collections.emptySet())       // aún no modelado
                        .direcciones(Collections.emptyList()) // aún no modelado
                        .build()
        );
    }

    @Override
    public Flux<Restaurante> mapFlux(Flux<RestauranteEntity> o) {
        return o.map(entity ->
                Restaurante.builder()
                        .id(entity.getId())
                        .nombre(entity.getNombre())
                        .description(entity.getDescription())
                        .iconUrl(entity.getIconUrl())
                        .tags(
                                entity.getTags() != null
                                        ? Arrays.asList(entity.getTags())
                                        : Collections.emptyList()
                        )
                        .platos(Collections.emptySet())
                        .direcciones(Collections.emptyList())
                        .build()
        );
    }
}