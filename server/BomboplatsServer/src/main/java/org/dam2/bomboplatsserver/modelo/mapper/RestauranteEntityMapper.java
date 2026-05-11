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

/**
 * Mapper encargado de convertir entre {@link Restaurante} (modelo de API)
 * y {@link RestauranteEntity} (entidad de base de datos).
 * Además de los campos propios del restaurante, recupera de forma reactiva
 * sus platos y direcciones asociados al construir el modelo de API.
 */
@Component
public class RestauranteEntityMapper implements EntityMapper<RestauranteEntity, Restaurante> {

    //@Autowired private PlatoEntityMapper platoMapper;
    @Autowired private IPlatoService platoService;
    @Autowired private IDireccionService direccionService;

    private String[] toStringArray(Object[] input) {
        if (input == null) return new String[0];
        return Arrays.stream(input)
                .map(o -> o != null ? o.toString() : null)
                .toArray(String[]::new);
    }

    /**
     * Convierte un {@link Restaurante} a su representación como entidad de base de datos.
     * Las listas de tags e iconos se transforman a arrays para su almacenamiento.
     * Los platos y direcciones no se persisten aquí; se gestionan por sus propios servicios.
     *
     * @param o Mono con el restaurante a convertir
     * @return Mono con la entidad resultante
     */
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

    /**
     * Convierte una {@link RestauranteEntity} a su representación como modelo de API.
     * Recupera de forma paralela los platos y direcciones asociados al restaurante
     * antes de construir el objeto final.
     * Gestiona la conversión de arrays genéricos a listas de Strings,
     * ya que algunos drivers de base de datos devuelven los arrays como {@code Object[]}.
     *
     * @param o Mono con la entidad a convertir
     * @return Mono con el restaurante resultante, incluyendo sus platos y direcciones
     */
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

    /**
     * Convierte un flujo de entidades {@link RestauranteEntity} a un flujo de objetos {@link Restaurante}.
     * Cada entidad se procesa individualmente mediante {@link #unmap}, lo que incluye
     * la recuperación de sus platos y direcciones.
     *
     * @param o Flux con las entidades a convertir
     * @return Flux con los restaurantes resultantes
     */
    @Override
    public Flux<Restaurante> mapFlux(Flux<RestauranteEntity> o) {
        return o.flatMap(entity -> unmap(Mono.just(entity)));
    }
}