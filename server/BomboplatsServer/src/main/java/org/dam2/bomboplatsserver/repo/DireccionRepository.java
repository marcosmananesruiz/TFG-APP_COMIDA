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

    /**
     * Encontrar una dirección según su IdUser
     * @param idUser Id del usuario con la dirección
     * @return {@link Flux}<{@link DireccionEntity}> con todas las direcciones del usuario
     */
    Flux<DireccionEntity> findByIdUser(String idUser);

    /**
     * Encontrar una dirección según su IdRestaurante;
     * @param idRestaurante Id del restaurante con la dirección
     * @return {@link Flux}<{@link DireccionEntity}> con todas las direcciones del restaurante
     */
    Flux<DireccionEntity> findByIdRestaurante(String idRestaurante);

    /**
     * Obtener el IdRestaurante de una dirección según su id
     * @param id Id de la direccion
     * @return {@link Mono}<{@link String}> con el IdRestaurante de la dirección
     */
    Mono<String> findIdRestauranteById(String id);

    /**
     * Obtener el IdUsuario de una dirección según su id
     * @param id Id de la dirección
     * @return {@link Mono}<{@link String}> con el IdUsuario de la dirección
     */
    @Query("SELECT id_usuario FROM direcciones WHERE id = :id")
    Mono<String> findIdUserById(String id);

    /**
     * Obtener el siguiente número para el Id de una dirección
     * @return {@link Mono}<{@link Long}> con el numero del id
     */
    @Query("SELECT nextval('direccion_seq')")
    Mono<Long> getNextID();

    /**
     * Obtener todas las Id de las direcciones
     * @return {@link Flux}<{@link String}>
     */
    @Query("SELECT id FROM direcciones")
    Flux<String> getIDs();
}
