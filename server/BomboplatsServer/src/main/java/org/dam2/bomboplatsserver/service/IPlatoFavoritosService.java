package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.dam2.bomboplatsserver.modelo.entity.PlatoFavoritosEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlatoFavoritosService {

    Flux<PlatoEntity> getPlatosFavoritosOf(String userId);
    Flux<PlatoFavoritosEntity> findAll();
    Mono<Boolean> register(PlatoFavoritosEntity platoFavoritosEntity);
    Mono<Boolean> delete(Long id);
}
