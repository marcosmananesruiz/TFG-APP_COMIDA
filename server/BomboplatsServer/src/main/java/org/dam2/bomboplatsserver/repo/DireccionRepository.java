package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DireccionRepository extends ReactiveCrudRepository<DireccionEntity, String> {

    @Query("SELECT * FROM DIRECCIONES WHERE idResidente = :user_id")
    Flux<DireccionEntity> findDireccionByResidentId(String userId);

    Mono<String> findIdResidenteById(String id);

    @Query("SELECT nextval('dir_seq')")
    Mono<Long> getNextID();
}
