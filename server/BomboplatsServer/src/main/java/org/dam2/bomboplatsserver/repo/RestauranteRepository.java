package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RestauranteRepository extends ReactiveCrudRepository<RestauranteEntity, String> {


    Flux<RestauranteEntity> findByNombre(String nombre);

    Flux<RestauranteEntity> findByNombreIgnoreCase(String nombre);

    Flux<RestauranteEntity> findByNombreContainingIgnoreCase(String nombre);

    Flux<RestauranteEntity> findByDescriptionContainingIgnoreCase(String description);

    @Query("SELECT nextval('restaurante_seq')")
    Mono<Long> getNextID();

    @Query("SELECT * FROM RESTAURANTES r WHERE :tag = ANY(r.tags)")
    Flux<RestauranteEntity> findByTag(String tag);

}
