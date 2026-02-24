package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRestauranteService {


    Mono<RestauranteEntity> findById(String id);
    Flux<RestauranteEntity> findAll();
    Mono<Boolean> register(RestauranteEntity restauranteEntity);
    Mono<Boolean> update(RestauranteEntity restauranteEntity);
    Mono<Boolean> deleteRestauranteById(String id);


    Flux<RestauranteEntity> findByNombre(String nombre);
    Flux<RestauranteEntity> findByNombreContaining(String nombre);
    Flux<RestauranteEntity> findByDescriptionContaining(String description);
    Flux<RestauranteEntity> findByTag(String tag);
}