package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Restaurante;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
public class RestauranteEntityMapper implements EntityMapper<RestauranteEntity, Restaurante> {

    @Autowired
    private PlatoEntityMapper platoMapper;

    private String[] toStringArray(Object[] input) {
        if (input == null) return new String[0];
        return Arrays.stream(input)
                .map(o -> o != null ? o.toString() : null)
                .toArray(String[]::new);
    }

    @Override
    public Mono<RestauranteEntity> map(Mono<Restaurante> o) {
        return o.map(restaurante -> RestauranteEntity.builder()
                .id(restaurante.getId())
                .nombre(restaurante.getNombre())
                .tags(restaurante.getTags() != null
                        ? restaurante.getTags().toArray(new String[0])
                        : new String[0])
                .iconUrl(restaurante.getIconUrls() != null
                        ? restaurante.getIconUrls().toArray(new String[0])
                        : new String[0])
                .description(restaurante.getDescription())
                .rating(restaurante.getRating())
                .build());
    }

    @Override
    public Mono<Restaurante> unmap(Mono<RestauranteEntity> o) {
        return o.map(entity -> {
            String[] tags = entity.getTags() instanceof Object[]
                    ? toStringArray((Object[]) entity.getTags())
                    : entity.getTags();

            String[] iconUrls = entity.getIconUrl() instanceof Object[]
                    ? toStringArray((Object[]) entity.getIconUrl())
                    : entity.getIconUrl();

            return Restaurante.builder()
                    .id(entity.getId())
                    .nombre(entity.getNombre())
                    .tags(tags != null ? Arrays.asList(tags) : List.of())
                    .iconUrls(iconUrls != null ? Arrays.asList(iconUrls) : List.of())
                    .description(entity.getDescription())
                    .rating(entity.getRating())
                    .build();
        });
    }

    @Override
    public Flux<Restaurante> mapFlux(Flux<RestauranteEntity> o) {
        return o.flatMap(entity -> unmap(Mono.just(entity)));
    }
}