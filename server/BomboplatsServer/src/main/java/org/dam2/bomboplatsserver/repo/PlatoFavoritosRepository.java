package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.PlatoFavoritosEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlatoFavoritosRepository extends ReactiveCrudRepository<PlatoFavoritosEntity, Long> {

    /**
     * Encontrar los platos favoritos de un usuario según su id
     * @param userId Id del usuario
     * @return {@link Flux}<{@link PlatoFavoritosEntity}> con los platos favoritos del usuario
     */
    Flux<PlatoFavoritosEntity> findByUserId(String userId);

    /**
     * Borrar un plato favorito según el ID del usuario y el del plato
     * @param userId Id del usuario
     * @param platoId Id del plato
     * @return {@link Mono}<{@link Void}>
     */
    Mono<Void> deleteByUserIdAndPlatoId(String userId, String platoId);
}
