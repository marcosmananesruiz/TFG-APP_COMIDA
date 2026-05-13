package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.User;
import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.dam2.bomboplatsserver.modelo.entity.PlatoFavoritosEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlatoFavoritosService {


    /**
     * Obtener los platos favoritos de un usuario
     * @param userId Id del usuario
     * @return {@link Flux}<{@link Plato}> con los platos favoritos del usuario
     */
    Flux<Plato> getPlatosFavoritosOf(String userId);

    /**
     * Obtener todos los platos favoritos
     * @return {@link Flux}<{@link PlatoFavoritosEntity}> con todos los platos favoritos
     */
    Flux<PlatoFavoritosEntity> findAll();

    /**
     * Registrar un plato favorito
     * @param platoFavoritosEntity El plato favorito
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se ha registrado correctamente, o {@code false} si no
     */
    Mono<Boolean> register(PlatoFavoritosEntity platoFavoritosEntity);

    /**
     * Eliminar un plato favorito
     * @param id Id del plato favorito
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se ha borrado correctamente, o {@code false} si no
     */
    Mono<Boolean> delete(Long id);

    /**
     * Borrar un plato favorito segun su usuario y plato
     * @param userId Id del usuario
     * @param platoId Id del plato
     * @return {@link Mono}<{@link Void}>
     */
    Mono<Void> deleteByUserIdAndPlatoId(String userId, String platoId);

    /**
     * Asignar un plato como favortito a un usuario
     * @param plato Plato a asignar
     * @param userId Id del usuario
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se asigno correctamente, o {@code false} si no
     */
    Mono<Boolean> asignarFavorito(Plato plato, String userId);

    /**
     * Asignar un plato como favortito a un usuario
     * @param plato Plato a asignar
     * @param user Usuario a asignar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se asigno correctamente, o {@code false} si no
     */
    Mono<Boolean> asignarFavorito(Plato plato, User user);

    /**
     * Asignar un plato como favortito a un usuario
     * @param platoId Id del plato
     * @param user Usuario a asignar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se asigno correctamente, o {@code false} si no
     */
    Mono<Boolean> asignarFavorito(String platoId, User user);

    /**
     * Asignar un plato como favortito a un usuario
     * @param platoId Id del plato
     * @param userId Id del usuario
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se asigno correctamente, o {@code false} si no
     */
    Mono<Boolean> asignarFavorito(String platoId, String userId);
}
