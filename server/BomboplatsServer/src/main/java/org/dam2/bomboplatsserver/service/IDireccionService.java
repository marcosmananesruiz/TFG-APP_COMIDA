package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Marcos Mañanes
 */
public interface IDireccionService {

    /**
     * Encuentra una direccion según su ID
     * @param id id a buscar
     * @return {@link Mono}<{@link DireccionEntity}> con ese ID o {@link Mono#empty()} si no existe
     */
    Mono<DireccionEntity> findById(String id);

    /**
     * Encuentra todos las direcciones en la base de datos
     * @return {@link Flux}<{@link DireccionEntity}> con todas las direcciones
     */
    Flux<DireccionEntity> findAll();

    /**
     * Registra un direccion en la base de datos
     * @param direccionEntity Direccion a registrar
     * @return {@link Mono}<{@link Boolean}>: {@code true} si se registro correctamente, {@code false} si ya existia ese registro o se produjo un error
     */
    Mono<Boolean> register(DireccionEntity direccionEntity);

    /**
     * Actualiza la información de un direccion en la base de datos
     * @param direccionEntity Dirección a actualizar
     * @return {@link Mono}<{@link Boolean}>: {@code true} si se actualizó correctamente, {@code false} si ya no existe ese registro o se produjo un error
     */
    Mono<Boolean> update(DireccionEntity direccionEntity);

    /**
     * Borra un direccion de la base de datos
     * @param direccionEntity Dirección a borrar
     * @see #deleteDireccionByID(String)
     * @return {@link Mono}<{@link Boolean}>: {@code true} si se borro correctamente, {@code false} si no existe ese registro o se produjo un error
     */
    Mono<Boolean> deleteDireccion(DireccionEntity direccionEntity);

    /**
     * Borra un direccion de la base de datos
     * @param id id de la dirección a borrar
     * @see #deleteDireccion(DireccionEntity)
     * @return {@link Mono}<{@link Boolean}>: {@code true} si se borro correctamente, {@code false} si no existe ese registro o se produjo un error
     */
    Mono<Boolean> deleteDireccionByID(String id);

    /**
     * Obtener el {@code idUser} de la direccion según su id
     * @param id id de la dirección
     * @return {@link Mono}<{@link String}> con él {@code idUser}, o {@link Mono#empty()} si no existe esa direccion
     */
    Mono<String> getUserID(String id);

    /**
     * Obtener el {@code idRestaurante} de la direccion según su id
     * @param id {@link String} id de la dirección
     * @return {@link Mono}<{@link String}> con él {@code idRestaurante}, o {@link Mono#empty()} si no existe esa dirección
     */
    Mono<String> getRestauranteID(String id);

    /**
     * Obtener todas las direcciones de un usuario
     * @param userEntity Usuario del que queremos las direcciones
     * @return {@link Flux}<{@link DireccionEntity}> con todas las direcciones del usuario. {@link Flux#empty()} en caso de que no haya ninguna
     */
    Flux<DireccionEntity> getDireccionesOfUser(UserEntity userEntity);

    /**
     * Obtener todas las direcciones de un usuario segun su id
     * @param userId Id del usuario del que queremos las direcciones
     * @return {@link Flux}<{@link DireccionEntity}> con todas las direcciones del usuario. {@link Flux#empty()} en caso de que no haya ninguna
     */
    Flux<DireccionEntity> getDireccionesOfUser(String userId);

    /**
     * Obtener todas las direcciones de un restaurante
     * @param restauranteEntity Restaurante del que queremos las direcciones
     * @return {@link Flux}<{@link DireccionEntity}> con todas las direcciones del restaurante. {@link Flux#empty()} en caso de que no haya ninguna
     */
    Flux<DireccionEntity> getDireccionesOfRestaurante(RestauranteEntity restauranteEntity);

    /**
     * Obtener todas las direcciones de un restaurante
     * @param restauranteId Id del restaurante del que queremos las direcciones
     * @return {@link Flux}<{@link DireccionEntity}> con todas las direcciones del restaurante. {@link Flux#empty()} en caso de que no haya ninguna
     */
    Flux<DireccionEntity> getDireccionesOfRestaurante(String restauranteId);
}
