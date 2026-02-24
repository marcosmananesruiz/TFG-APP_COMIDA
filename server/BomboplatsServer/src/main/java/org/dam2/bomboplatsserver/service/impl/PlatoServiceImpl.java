package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.dam2.bomboplatsserver.repo.PlatoRepository;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlatoServiceImpl implements IPlatoService {

    @Autowired private PlatoRepository repo;
    @Autowired private R2dbcEntityTemplate template;

    public static Logger LOGGER = LoggerFactory.getLogger(PlatoServiceImpl.class);

    // Cambiado UNIQUE_CHAR a "T" según tu formato
    private final String UNIQUE_CHAR = "T";

    @Override
    public Mono<PlatoEntity> findById(String id) {
        return this.repo.findById(id);
    }

    @Override
    public Flux<PlatoEntity> findAll() {
        return this.repo.findAll();
    }

    @Override
    public Mono<Boolean> register(PlatoEntity platoEntity) {

        return this.repo.getNextID()
                // Generar ID con formato T00001, T00002, etc.
                .map(idNumber -> String.format("%s%05d", UNIQUE_CHAR, idNumber))
                .flatMap(id -> {
                    platoEntity.setId(id);
                    return this.template.insert(PlatoEntity.class)
                            .using(platoEntity);
                }).thenReturn(true)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    LOGGER.error("{}: Se ha intentado registrar un plato con ID {}, el cual ya existe",
                            e.getMessage(), platoEntity.getId());
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<Boolean> update(PlatoEntity platoEntity) {
        return this.repo.findById(platoEntity.getId())
                .flatMap(existing -> this.repo.save(platoEntity)
                        .doOnNext(updatedEntity ->
                                LOGGER.info("Plato con ID {} actualizado", updatedEntity.getId()))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> deletePlatoById(String id) {
        return this.repo.findById(id)
                .flatMap(exists -> this.repo.deleteById(id)
                        .doOnSuccess(deletedId ->
                                LOGGER.info("Plato con ID {} eliminado", deletedId))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    @Override
    public Flux<PlatoEntity> findByIdRestaurante(String idRestaurante) {
        return this.repo.findByIdRestaurante(idRestaurante);
    }

    @Override
    public Flux<PlatoEntity> findByNombreContaining(String nombre) {
        return this.repo.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Flux<PlatoEntity> findByIdRestauranteAndNombreContaining(String idRestaurante, String nombre) {
        return this.repo.findByIdRestauranteAndNombreContainingIgnoreCase(idRestaurante, nombre);
    }

    @Override
    public Flux<PlatoEntity> findByTag(String tag) {
        return this.repo.findByTag(tag);
    }
}