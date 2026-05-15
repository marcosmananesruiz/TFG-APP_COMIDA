package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.dam2.bomboplatsserver.repo.PlatoRepository;
import org.dam2.bomboplatsserver.service.IPedidoService;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


@Service
public class PlatoServiceImpl implements IPlatoService {

    @Autowired private PlatoRepository repo;
    @Autowired private R2dbcEntityTemplate template;
    @Autowired @Lazy private IPedidoService pedidoService;
    public static Logger LOGGER = LoggerFactory.getLogger(PlatoServiceImpl.class);
    //Prefijo que usamos para generar los IDs de los platos en la bbdd
    private final String UNIQUE_CHAR = "T";

    private String[] toStringArray(Object[] input) {
        if (input == null) return new String[0];
        return Arrays.stream(input)
                .map(o -> o != null ? o.toString() : null)
                .toArray(String[]::new);
    }

    /**
     * Convierte una {@link PlatoEntity} al DTO {@link Plato}.
     * Gestiona la conversión de arrays genéricos a listas de Strings.
     *
     * @param entity entidad a convertir
     * @return DTO resultante
     */
    private Plato toDTO(PlatoEntity entity) {
        String[] tags = entity.getTags() instanceof Object[]
                ? toStringArray((Object[]) entity.getTags())
                : entity.getTags();
        String[] mods = entity.getPossibleModifications() instanceof Object[]
                ? toStringArray((Object[]) entity.getPossibleModifications())
                : entity.getPossibleModifications();

        return Plato.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .description(entity.getDescription())
                .iconUrl(entity.getIconUrl())
                .tags(tags != null ? Arrays.asList(tags) : List.of())
                .possibleModifications(mods != null ? Arrays.asList(mods) : List.of())
                .precio(entity.getPrecio())
                .build();
    }

    /**
     * Convierte un DTO {@link Plato} a su entidad {@link PlatoEntity}.
     * Las listas de tags y modificaciones se convierten a arrays para su almacenamiento.
     *
     * @param plato DTO a convertir
     * @return entidad resultante
     */
    private PlatoEntity toEntity(Plato plato) {
        return PlatoEntity.builder()
                .id(plato.getId())
                .nombre(plato.getNombre())
                .description(plato.getDescription())
                .iconUrl(plato.getIconUrl())
                .tags(plato.getTags() != null ? plato.getTags().toArray(new String[0]) : new String[0])
                .possibleModifications(plato.getPossibleModifications() != null
                        ? plato.getPossibleModifications().toArray(new String[0])
                        : new String[0])
                .precio(plato.getPrecio())
                .build();
    }


    @Override
    public Mono<Plato> findById(String id) {
        return this.repo.findById(id).map(this::toDTO);
    }


    @Override
    public Flux<Plato> findAll() {
        return this.repo.findAll().map(this::toDTO);
    }

    @Override
    public Mono<Plato> register(Plato plato) {
        PlatoEntity entity = toEntity(plato);
        return this.repo.getNextID()
                .map(idNumber -> String.format("%s%05d", UNIQUE_CHAR, idNumber))
                .flatMap(id -> {
                    entity.setId(id);
                    return this.template.insert(PlatoEntity.class).using(entity);
                })
                .map(this::toDTO)
                .onErrorResume(e -> {
                    LOGGER.error("Error al registrar plato: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }


    @Override
    public Mono<Boolean> update(Plato plato) {
        PlatoEntity entity = toEntity(plato);
        return this.repo.findById(entity.getId())
                .flatMap(existing -> {
                    entity.setIdRestaurante(existing.getIdRestaurante());
                    return this.repo.save(entity)
                            .doOnNext(u -> LOGGER.info("Plato con ID {} actualizado", u.getId()))
                            .thenReturn(true);
                }).defaultIfEmpty(false);
    }


    @Override
    public Mono<Boolean> deletePlatoById(String id) {
        return this.repo.findById(id)
                .flatMap(exists ->
                        pedidoService.existsByPlatoId(id)
                                .flatMap(hayPedidos -> {
                                    if (hayPedidos) {
                                        return pedidoService.findByPlatoId(id)
                                                .flatMap(pedido -> pedidoService.deletePedidoById(pedido.getId()))
                                                .then(this.repo.deleteById(id));
                                    } else {
                                        return this.repo.deleteById(id);
                                    }
                                })
                                .thenReturn(true)
                ).defaultIfEmpty(false);
    }


    @Override
    public Flux<Plato> findByIdRestaurante(String idRestaurante) {
        return this.repo.findByIdRestaurante(idRestaurante).map(this::toDTO);
    }


    @Override
    public Flux<Plato> findByNombreContaining(String nombre) {
        return this.repo.findByNombreContainingIgnoreCase(nombre).map(this::toDTO);
    }


    @Override
    public Flux<Plato> findByIdRestauranteAndNombreContaining(String idRestaurante, String nombre) {
        return this.repo.findByIdRestauranteAndNombreContainingIgnoreCase(idRestaurante, nombre).map(this::toDTO);
    }


    @Override
    public Flux<Plato> findByTag(String tag) {
        return this.repo.findByTag(tag).map(this::toDTO);
    }


    @Override
    public Mono<Plato> registerConRestaurante(Plato plato, String idRestaurante) {
        PlatoEntity entity = toEntity(plato);
        entity.setIdRestaurante(idRestaurante);
        return this.repo.getNextID()
                .map(idNumber -> String.format("%s%05d", UNIQUE_CHAR, idNumber))
                .flatMap(id -> {
                    entity.setId(id);
                    return this.template.insert(PlatoEntity.class).using(entity);
                })
                .map(this::toDTO)
                .onErrorResume(e -> {
                    LOGGER.error("Error al registrar plato con restaurante: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

}