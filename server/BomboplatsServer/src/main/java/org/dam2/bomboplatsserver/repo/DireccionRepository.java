package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface DireccionRepository extends ReactiveCrudRepository<DireccionEntity, String> {

    Flux<DireccionEntity> findByIdUser(String idUser);
    Flux<DireccionEntity> findByIdRestaurante(String idRestaurante);

    Mono<String> findIdRestauranteById(String id);
    Mono<String> findIdUserById(String id);
    
    @Query("SELECT nextval('direccion_seq')")
    Mono<Long> getNextID();

}
