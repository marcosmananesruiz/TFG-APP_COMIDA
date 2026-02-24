package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlatoRepository extends ReactiveCrudRepository<PlatoEntity, String> {

    @Query("SELECT nextval('plato_seq')")
    Mono<Long> getNextID();

    Flux<PlatoEntity> findByIdRestaurante(String idRestaurante);

    Flux<PlatoEntity> findByNombreContainingIgnoreCase(String nombre);

    Flux<PlatoEntity> findByIdRestauranteAndNombreContainingIgnoreCase(String idRestaurante, String nombre);

    @Query("SELECT * FROM PLATOS p WHERE :tag = ANY(p.tags)")
    Flux<PlatoEntity> findByTag(String tag);

}