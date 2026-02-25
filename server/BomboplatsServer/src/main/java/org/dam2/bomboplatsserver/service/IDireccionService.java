package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDireccionService {

    Mono<DireccionEntity> findById(String id);
    Flux<DireccionEntity> findAll();
    Mono<Boolean> register(DireccionEntity direccionEntity);
    Mono<Boolean> update(DireccionEntity direccionEntity);
    Mono<Boolean> deleteDireccion(DireccionEntity direccionEntity);
    Mono<Boolean> deleteDireccionByID(String id);
    Mono<String> getUserID(String id);
    Mono<String> getRestauranteID(String id);

    Flux<DireccionEntity> getDireccionesOfUser(UserEntity userEntity);
    Flux<DireccionEntity> getDireccionesOfUser(String userId);
    Flux<DireccionEntity> getDireccionesOfRestaurante(RestauranteEntity restauranteEntity);
    Flux<DireccionEntity> getDireccionesOfRestaurante(String restauranteId);
}
