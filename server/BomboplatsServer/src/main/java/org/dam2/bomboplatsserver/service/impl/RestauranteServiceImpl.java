package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplats.api.Restaurante;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.dam2.bomboplatsserver.repo.RestauranteRepository;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.dam2.bomboplatsserver.service.IRestauranteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RestauranteServiceImpl implements IRestauranteService {

    @Autowired private RestauranteRepository repo;
    @Autowired private R2dbcEntityTemplate template;
    //@Autowired private RestauranteEntityMapper mapper;
    @Autowired private IPlatoService platoService;
    @Autowired private IDireccionService direccionService;

    public static Logger LOGGER = LoggerFactory.getLogger(RestauranteServiceImpl.class);
    private final String UNIQUE_CHAR = "R";

    private Mono<Restaurante> enrich(RestauranteEntity entity) {
        Mono<List<org.dam2.bomboplats.api.Plato>> platosMono = platoService
                .findByIdRestaurante(entity.getId())
                .collectList()
                .doOnNext(platos -> LOGGER.info("Platos encontrados para {}: {}", entity.getId(), platos.size()));

        Mono<List<org.dam2.bomboplats.api.Direccion>> direccionesMono = direccionService
                .getDireccionesOfRestaurante(entity.getId())
                .collectList()
                .doOnNext(dirs -> LOGGER.info("Direcciones encontradas para {}: {}", entity.getId(), dirs.size()));

        return Mono.zip(platosMono, direccionesMono)
                .doOnSuccess(t -> LOGGER.info("Enrich completado para {}", entity.getId()))
                .map(tuple -> {
                    String[] tags = entity.getTags() instanceof Object[]
                            ? toStringArray((Object[]) entity.getTags())
                            : entity.getTags();
                    String[] iconUrls = entity.getIconUrl() instanceof Object[]
                            ? toStringArray((Object[]) entity.getIconUrl())
                            : entity.getIconUrl();

                    return Restaurante.builder()
                            .id(entity.getId())
                            .nombre(entity.getNombre())
                            .description(entity.getDescription())
                            .tags(tags != null ? java.util.Arrays.asList(tags) : java.util.List.of())
                            .iconUrls(iconUrls != null ? java.util.Arrays.asList(iconUrls) : java.util.List.of())
                            .rating(entity.getRating())
                            .platos(new java.util.HashSet<>(tuple.getT1()))
                            .direcciones(tuple.getT2())
                            .build();
                });
    }

    private String[] toStringArray(Object[] input) {
        if (input == null) return new String[0];
        return java.util.Arrays.stream(input)
                .map(o -> o != null ? o.toString() : null)
                .toArray(String[]::new);
    }

    private RestauranteEntity toEntity(Restaurante restaurante) {
        return RestauranteEntity.builder()
                .id(restaurante.getId())
                .nombre(restaurante.getNombre())
                .description(restaurante.getDescription())
                .tags(restaurante.getTags() != null
                        ? restaurante.getTags().toArray(new String[0])
                        : new String[0])
                .iconUrl(restaurante.getIconUrls() != null
                        ? restaurante.getIconUrls().toArray(new String[0])
                        : new String[0])
                .rating(restaurante.getRating())
                .build();
    }

    @Override
    public Mono<Restaurante> findById(String id) {
        return this.repo.findById(id).flatMap(this::enrich);
    }

    @Override
    public Flux<Restaurante> findAll() {
        return this.repo.findAll().flatMap(this::enrich);
    }

    @Override
    public Mono<Restaurante> register(Restaurante restaurante) {
        RestauranteEntity entity = toEntity(restaurante);
        return this.repo.getNextID()
                .map(idNumber -> this.UNIQUE_CHAR + idNumber)
                .flatMap(id -> {
                    entity.setId(id);
                    return this.template.insert(RestauranteEntity.class).using(entity);
                })
                .flatMap(this::enrich)
                .onErrorResume(e -> {
                    LOGGER.error("Error al registrar restaurante: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Boolean> update(Restaurante restaurante) {
        RestauranteEntity entity = toEntity(restaurante);
        return this.repo.findById(entity.getId())
                .flatMap(existing -> this.repo.save(entity)
                        .doOnNext(u -> LOGGER.info("Restaurante con ID {} actualizado", u.getId()))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> deleteRestauranteById(String id) {
        return this.repo.findById(id)
                .flatMap(exists -> this.repo.deleteById(id)
                        .doOnSuccess(d -> LOGGER.info("Restaurante con ID {} eliminado", d))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    @Override
    public Flux<Restaurante> findByNombre(String nombre) {
        return this.repo.findByNombre(nombre).flatMap(this::enrich);
    }

    @Override
    public Flux<Restaurante> findByNombreContaining(String nombre) {
        return this.repo.findByNombreContainingIgnoreCase(nombre).flatMap(this::enrich);
    }

    @Override
    public Flux<Restaurante> findByDescriptionContaining(String description) {
        return this.repo.findByDescriptionContainingIgnoreCase(description).flatMap(this::enrich);
    }

    @Override
    public Flux<Restaurante> findByTag(String tag) {
        return this.repo.findByTag(tag).flatMap(this::enrich);
    }
}