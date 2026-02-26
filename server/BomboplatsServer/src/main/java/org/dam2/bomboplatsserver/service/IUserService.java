package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Marcos Mañanes
 */
public interface IUserService {

    /**
     * Obtener un usuario según su Id
     * @param id Id del usuario a buscar
     * @return {@link Mono}<{@link UserEntity}> con el usuario a buscar. {@link Mono#empty()} si no existe ninguno
     */
    Mono<UserEntity> findByID(String id);

    /**
     * Obtener todos los usuarios de la base de datos
     * @return {@link Flux}<{@link UserEntity}> con todos los usuarios
     */
    Flux<UserEntity> findAll();

    /**
     * Registrar un usuario en la base de datos
     * @param userEntity Usuario a registrar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se registro correctamente, o {@code false} si ese registro ya existe o se ha producido un error
     */
    Mono<Boolean> register(UserEntity userEntity);

    /**
     * Actualiza un usuario de la base de datos
     * @param userEntity Usuario a actualizar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se actualizo correctamente, o {@code false} si ese registro no existe o se ha producido un error
     */
    Mono<Boolean> update(UserEntity userEntity);

    /**
     * Borra un usuario de la base de datos
     * @param userEntity Usuario a borrar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se elimino correctamente, o {@code false} si ese registro no existe o se ha producido un error
     */
    Mono<Boolean> deleteUser(UserEntity userEntity);

    /**
     * Borra un usuario de la base de datos segun su Id
     * @param id Id del usuario a borrar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se elimino correctamente, o {@code false} si ese registro no existe o se ha producido un error
     */
    Mono<Boolean> deleteUserByID(String id);

    /**
     * Obtener la contraseña almacenada en la base de datos de un usuario segun su Id
     * @param id Id del usuario
     * @return {@link Mono}<{@link String}> con la contraseña del usuario. {@link Mono#empty()} si no se encuentra
     * @apiNote (¡La contraseña viene hasheada!)
     */
    Mono<String> getPassword(String id);

    /**
     * Obtener un usuario según su email
     * @param email email del usuario
     * @return {@link Mono}<{@link UserEntity}> con el usuario. {@link Mono#empty()} si no se encuentra ningún usuario
     */
    Mono<UserEntity> findByEmail(String email);
}
