package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/**
 * Mapper encargado de convertir entre {@link Plato} (modelo de API)
 * y {@link PlatoEntity} (entidad de base de datos).
 */
@Component
public class PlatoEntityMapper implements EntityMapper<PlatoEntity, Plato> {

    private String[] toStringArray(Object[] input) {
        if (input == null) return new String[0];
        return Arrays.stream(input)
                .map(o -> o != null ? o.toString() : null)
                .toArray(String[]::new);
    }

    /**
     * Convierte un {@link Plato} a su representación como entidad de base de datos.
     * Las listas de tags y modificaciones se transforman a arrays para su almacenamiento.
     *
     * @param o Mono con el plato a convertir
     * @return Mono con la entidad resultante
     */
    @Override
    public Mono<PlatoEntity> map(Mono<Plato> o) {
        return o.map(plato -> PlatoEntity.builder()
                .id(plato.getId())
                .nombre(plato.getNombre())
                .description(plato.getDescription())
                .iconUrl(plato.getIconUrl())
                .tags(plato.getTags() != null
                        ? plato.getTags().toArray(new String[0])
                        : new String[0])
                .possibleModifications(plato.getPossibleModifications() != null
                        ? plato.getPossibleModifications().toArray(new String[0])
                        : new String[0])
                .precio(plato.getPrecio())
                .build());
    }

    /**
     * Convierte una {@link PlatoEntity} a su representación como modelo de API.
     * Gestiona la conversión de arrays genéricos a listas de Strings,
     * ya que algunos drivers de base de datos devuelven los arrays como {@code Object[]}.
     *
     * @param o Mono con la entidad a convertir
     * @return Mono con el plato resultante
     */
    @Override
    public Mono<Plato> unmap(Mono<PlatoEntity> o) {
        return o.map(entity -> {
            String[] tags = entity.getTags() instanceof Object[]
                    ? toStringArray((Object[]) entity.getTags())
                    : entity.getTags();

            String[] modifications = entity.getPossibleModifications() instanceof Object[]
                    ? toStringArray((Object[]) entity.getPossibleModifications())
                    : entity.getPossibleModifications();

            return Plato.builder()
                    .id(entity.getId())
                    .nombre(entity.getNombre())
                    .description(entity.getDescription())
                    .iconUrl(entity.getIconUrl())
                    .tags(tags != null ? Arrays.asList(tags) : List.of())
                    .possibleModifications(modifications != null ? Arrays.asList(modifications) : List.of())
                    .precio(entity.getPrecio())
                    .build();
        });
    }
    /**
     * Convierte un flujo de entidades {@link PlatoEntity} a un flujo de objetos {@link Plato}.
     *
     * @param o Flux con las entidades a convertir
     * @return Flux con los platos resultantes
     */
    @Override
    public Flux<Plato> mapFlux(Flux<PlatoEntity> o) {
        return o.flatMap(entity -> unmap(Mono.just(entity)));
    }
}