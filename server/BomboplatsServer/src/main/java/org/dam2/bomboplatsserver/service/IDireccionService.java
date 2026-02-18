package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDireccionService {

    Mono<DireccionEntity> findById(String id);
    Mono<Boolean> register(DireccionEntity direccionEntity);
    Mono<Boolean> update(DireccionEntity direccionEntity);
    Mono<Boolean> deleteDireccion(DireccionEntity direccionEntity);
    Mono<Boolean> deleteDireccionByID(String id);

    Flux<DireccionEntity> getDireccionesOfUser(UserEntity userEntity);
    //Flux<DireccionEntity> getDireccionesOfRestaurante(RestauranteEntity restauranteEntity)
}
