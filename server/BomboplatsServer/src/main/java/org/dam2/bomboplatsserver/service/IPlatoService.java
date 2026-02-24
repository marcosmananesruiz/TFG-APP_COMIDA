package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlatoService {

    Mono<PlatoEntity> findById(String id);
    Flux<PlatoEntity> findAll();
    Mono<Boolean> register(PlatoEntity platoEntity);
    Mono<Boolean> update(PlatoEntity platoEntity);
    Mono<Boolean> deletePlatoById(String id);

    Flux<PlatoEntity> findByIdRestaurante(String idRestaurante);
    Flux<PlatoEntity> findByNombreContaining(String nombre);
    Flux<PlatoEntity> findByIdRestauranteAndNombreContaining(String idRestaurante, String nombre);
    Flux<PlatoEntity> findByTag(String tag);
}