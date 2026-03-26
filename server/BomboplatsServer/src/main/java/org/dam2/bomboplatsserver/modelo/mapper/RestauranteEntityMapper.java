package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Restaurante;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
public class RestauranteEntityMapper implements EntityMapper<RestauranteEntity, Restaurante> {

    @Autowired private PlatoEntityMapper platoMapper;
    @Autowired private IPlatoService platoService;
    @Autowired private IDireccionService direccionService;

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
        return o.flatMap(entity -> {
            String[] tags = entity.getTags() instanceof Object[]
                    ? toStringArray((Object[]) entity.getTags())
                    : entity.getTags();

            String[] iconUrls = entity.getIconUrl() instanceof Object[]
                    ? toStringArray((Object[]) entity.getIconUrl())
                    : entity.getIconUrl();

            Mono<List<org.dam2.bomboplats.api.Plato>> platosMono = platoService
                    .findByIdRestaurante(entity.getId())
                    .flatMap(platoEntity -> platoMapper.unmap(Mono.just(platoEntity)))
                    .collectList();

            Mono<List<org.dam2.bomboplats.api.Direccion>> direccionesMono = direccionService
                    .getDireccionesOfRestaurante(entity.getId())
                    .collectList();

            String[] finalIconUrls = iconUrls;
            String[] finalTags = tags;

            return Mono.zip(platosMono, direccionesMono)
                    .map(tuple -> Restaurante.builder()
                            .id(entity.getId())
                            .nombre(entity.getNombre())
                            .tags(finalTags != null ? Arrays.asList(finalTags) : List.of())
                            .iconUrls(finalIconUrls != null ? Arrays.asList(finalIconUrls) : List.of())
                            .description(entity.getDescription())
                            .rating(entity.getRating())
                            .platos(new java.util.HashSet<>(tuple.getT1()))
                            .direcciones(tuple.getT2())
                            .build());
        });
    }

    @Override
    public Flux<Restaurante> mapFlux(Flux<RestauranteEntity> o) {
        return o.flatMap(entity -> unmap(Mono.just(entity)));
    }
}