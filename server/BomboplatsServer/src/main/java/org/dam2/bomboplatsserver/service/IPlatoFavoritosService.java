package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.User;
import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.dam2.bomboplatsserver.modelo.entity.PlatoFavoritosEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlatoFavoritosService {

    Flux<Plato> getPlatosFavoritosOf(String userId);
    Flux<PlatoFavoritosEntity> findAll();
    Mono<Boolean> register(PlatoFavoritosEntity platoFavoritosEntity);
    Mono<Boolean> delete(Long id);
    Mono<Void> deleteByUserIdAndPlatoId(String userId, String platoId);
    Mono<Boolean> asignarFavorito(Plato plato, String userId);
    Mono<Boolean> asignarFavorito(Plato plato, User user);
    Mono<Boolean> asignarFavorito(String platoId, User user);
    Mono<Boolean> asignarFavorito(String platoId, String userId);
}
