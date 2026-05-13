package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {

    /**
     * Encontrar la contraseña de un usuario segun su id
     * @param id Id del usuario
     * @return {@link Mono}<{@link String}> con la contraseña del usuario
     * @apiNote ¡La contraseña viene hasheada!
     */
    @Query("SELECT password FROM usuarios WHERE ID = :id")
    Mono<String> findPasswordById(String id);

    /**
     * Obtener el siguiente número para el Id de un usuario
     * @return {@link Mono}<{@link Long}> con el numero del id
     */
    @Query("SELECT nextval('user_seq')")
    Mono<Long> getNextID();

    /**
     * Encontrar un usuario según su email
     * @param email Email del usuario
     * @return {@link Mono}<{@link UserEntity}> con el usuario correspondiente
     */
    Mono<UserEntity> findUserEntityByEmail(String email);

    /**
     * Comprueba si existe un usuario según su Id
     * @param email Email del usuario
     * @return {@link Mono}<{@link Boolean}> con {@code true} si existe un usuario con ese email, {@code false} si no
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Obtener todos los Ids de los usuarios
     * @return {@link Flux}<{@link String}> con todos los ids
     */
    @Query("SELECT id FROM usuarios")
    Flux<String> getIDs();
}
