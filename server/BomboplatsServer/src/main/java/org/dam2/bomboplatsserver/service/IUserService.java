package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.User;
import org.dam2.bomboplats.api.login.LoginAttempt;
import org.dam2.bomboplats.api.login.UserRegister;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserService {

    /**
     * Obtener un usuario según su Id
     * @param id Id del usuario a buscar
     * @return {@link Mono}<{@link UserEntity}> con el usuario a buscar. {@link Mono#empty()} si no existe ninguno
     */
    Mono<User> findByID(String id);

    /**
     * Obtener todos los usuarios de la base de datos
     * @return {@link Flux}<{@link UserEntity}> con todos los usuarios
     */
    Flux<User> findAll();

    /**
     * Registrar un usuario en la base de datos
     * @param userRegister Usuario a registrar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se registro correctamente, o {@code false} si ese registro ya existe o se ha producido un error
     */
    Mono<User> register(UserRegister userRegister);

    /**
     * Actualiza un usuario de la base de datos
     * @param user Usuario a actualizar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se actualizo correctamente, o {@code false} si ese registro no existe o se ha producido un error
     */
    Mono<Boolean> update(User user);

    /**
     * Borra un usuario de la base de datos
     * @param user Usuario a borrar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se elimino correctamente, o {@code false} si ese registro no existe o se ha producido un error
     */
    Mono<Boolean> deleteUser(User user);

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
    Mono<User> findByEmail(String email);

    /**
     * Verificar el login de un usuario
     * @param loginAttempt Informacion del Login
     * @return {@link Mono}<{@link Boolean}> con {@code true} si el login es correcto, o {@code false} si no
     */
    Mono<Boolean> verifyLogin(LoginAttempt loginAttempt);

    /**
     * Actualizar la contraseña de un usuario
     * @param userId Id del usuario
     * @param newPassword Nueva contraseña
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se actualizo correctamente, o {@code false} si no
     */
    Mono<Boolean> updatePassword(String userId, String newPassword);

    /**
     * Obtener los platos favoritos de un usuario
     * @param userId Id del usuario
     * @return {@link Flux}<{@link Plato}> con los platos favoritos del usuario
     */
    Flux<Plato> getPlatosFavoritos(String userId);

    /**
     * Obtener todos los Ids de los usuarios
     * @return {@link Flux}<{@link String}> con todos los Ids
     */
    Flux<String> getUserIds();

    /**
     * Guardar la entidad de un usuario
     * @param user Usuario a guardar
     * @return {@link Mono}<{@link Void}>
     * @apiNote Usado para evitar bucles en las dependencias
     */
    Mono<Void> saveUserEntity(User user);
}
