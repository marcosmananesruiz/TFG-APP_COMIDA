package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.dam2.bomboplatsserver.repo.RestauranteRepository;
import org.dam2.bomboplatsserver.service.IRestauranteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RestauranteServiceImpl implements IRestauranteService {

    @Autowired private RestauranteRepository repo;
    @Autowired private R2dbcEntityTemplate template;

    public static Logger LOGGER = LoggerFactory.getLogger(RestauranteServiceImpl.class);
    private final String UNIQUE_CHAR = "R";

    @Override
    public Mono<RestauranteEntity> findById(String id) {
        return this.repo.findById(id);
    }

    @Override
    public Flux<RestauranteEntity> findAll() {
        return this.repo.findAll();
    }

    @Override
    public Mono<Boolean> register(RestauranteEntity restauranteEntity) {

        return this.repo.getNextID()
                .map(idNumber -> this.UNIQUE_CHAR + idNumber)
                .flatMap(id -> {
                    restauranteEntity.setId(id);
                    return this.template.insert(RestauranteEntity.class)
                            .using(restauranteEntity);
                }).thenReturn(true)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    LOGGER.error("{}: Se ha intentado registrar un restaurante con ID {}, el cual ya existe",
                            e.getMessage(), restauranteEntity.getId());
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<Boolean> update(RestauranteEntity restauranteEntity) {
        return this.repo.findById(restauranteEntity.getId())
                .flatMap(existing -> this.repo.save(restauranteEntity)
                        .doOnNext(updatedEntity ->
                                LOGGER.info("Restaurante con ID {} actualizado", updatedEntity.getId()))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> deleteRestauranteById(String id) {
        return this.repo.findById(id)
                .flatMap(exists -> this.repo.deleteById(id)
                        .doOnSuccess(deletedId ->
                                LOGGER.info("Restaurante con ID {} eliminado", deletedId))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }


    @Override
    public Flux<RestauranteEntity> findByNombre(String nombre) {
        return this.repo.findByNombre(nombre);
    }

    @Override
    public Flux<RestauranteEntity> findByNombreContaining(String nombre) {
        return this.repo.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Flux<RestauranteEntity> findByDescriptionContaining(String description) {
        return this.repo.findByDescriptionContainingIgnoreCase(description);
    }

    @Override
    public Flux<RestauranteEntity> findByTag(String tag) {
        return this.repo.findByTag(tag);
    }
}