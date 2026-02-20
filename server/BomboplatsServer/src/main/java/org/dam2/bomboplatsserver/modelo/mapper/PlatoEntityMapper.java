package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

@Component
public class PlatoEntityMapper implements EntityMapper<PlatoEntity, Plato> {

    @Override
    public Mono<PlatoEntity> map(Mono<Plato> o) {
        return o.map(plato ->
                PlatoEntity.builder()
                        .id(plato.getId())
                        .nombre(plato.getNombre())
                        .description(plato.getDescription())
                        .iconUrl(plato.getIconUrl())
                        .tags(
                                plato.getTags() != null
                                        ? plato.getTags().toArray(new String[0])
                                        : null
                        )
                        .possibleModifications(
                                plato.getPossibleModifications() != null
                                        ? plato.getPossibleModifications().toArray(new String[0])
                                        : null
                        )
                        // idRestaurante se asigna en servicio cuando guardas
                        .build()
        );
    }

    @Override
    public Mono<Plato> unmap(Mono<PlatoEntity> o) {
        return o.map(entity ->
                Plato.builder()
                        .id(entity.getId())
                        .nombre(entity.getNombre())
                        .description(entity.getDescription())
                        .iconUrl(entity.getIconUrl())
                        .tags(
                                entity.getTags() != null
                                        ? Arrays.asList(entity.getTags())
                                        : Collections.emptyList()
                        )
                        .possibleModifications(
                                entity.getPossibleModifications() != null
                                        ? Arrays.asList(entity.getPossibleModifications())
                                        : Collections.emptyList()
                        )
                        .build()
        );
    }

    @Override
    public Flux<Plato> mapFlux(Flux<PlatoEntity> o) {
        return o.map(entity ->
                Plato.builder()
                        .id(entity.getId())
                        .nombre(entity.getNombre())
                        .description(entity.getDescription())
                        .iconUrl(entity.getIconUrl())
                        .tags(
                                entity.getTags() != null
                                        ? Arrays.asList(entity.getTags())
                                        : Collections.emptyList()
                        )
                        .possibleModifications(
                                entity.getPossibleModifications() != null
                                        ? Arrays.asList(entity.getPossibleModifications())
                                        : Collections.emptyList()
                        )
                        .build()
        );
    }
}