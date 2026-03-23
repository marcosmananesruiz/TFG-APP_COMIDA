package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.PlatoFavoritosEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlatoFavoritosRepository extends ReactiveCrudRepository<PlatoFavoritosEntity, Long> {

    Flux<PlatoFavoritosEntity> findByUserId(String userId);

    Mono<Void> deleteByUserIdAndPlatoId(String userId, String platoId);
}
