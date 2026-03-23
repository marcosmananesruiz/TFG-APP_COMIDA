package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Restaurante;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class RestauranteEntityMapper implements EntityMapper<RestauranteEntity, Restaurante> {

    @Autowired
    private PlatoEntityMapper platoMapper; // Para mapear los platos si los necesitas más adelante

    @Override
    public Mono<RestauranteEntity> map(Mono<Restaurante> o) {
        return o.map(restaurante -> RestauranteEntity.builder()
                .id(restaurante.getId())
                .nombre(restaurante.getNombre())
                .tags(restaurante.getTags() != null ? restaurante.getTags().toArray(new String[0]) : new String[0])
                .iconUrl(restaurante.getIconUrls() != null ? (String[]) restaurante.getIconUrls().toArray() : new String[0])
                .description(restaurante.getDescription())
                .rating(restaurante.getRating())
                .build());
    }

    @Override
    public Mono<Restaurante> unmap(Mono<RestauranteEntity> o) {
        return o.map(entity -> Restaurante.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .tags(entity.getTags() != null ? Arrays.asList(entity.getTags()) : List.of())
                .iconUrls(entity.getIconUrl() != null && entity.getIconUrl().length > 0 ? Collections.singletonList(entity.getIconUrl()[0]) : null)
                .description(entity.getDescription())
                .rating(entity.getRating())
                .build());
    }

    @Override
    public Flux<Restaurante> mapFlux(Flux<RestauranteEntity> o) {
        return o.flatMap(entity -> unmap(Mono.just(entity)));
    }
}