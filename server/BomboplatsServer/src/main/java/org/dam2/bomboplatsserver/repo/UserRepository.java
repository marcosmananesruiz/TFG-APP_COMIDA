package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {

    @Query("SELECT password FROM usuarios WHERE ID = :id")
    Mono<String> findPasswordById(String id);

    @Query("SELECT nextval('user_seq')")
    Mono<Long> getNextID();

    Mono<UserEntity> findUserEntityByEmail(String email);


}
