package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplats.api.*;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPedidoService {

    /**
     * Obtener el pedido según su ID
     * @param id {@link String} id del pedido
     * @return {@link Mono}<{@link PedidoEntity}> con ese ID, o {@link Mono#empty()} si no existe
     */
    Mono<Pedido> findById(String id);

    /**
     * Obtener todos los pedidos
     * @return {@link Flux}<{@link PedidoEntity}> con todos los pedidos
     */
    Flux<Pedido> findAll();

    /**
     * Registrar un pedido en la base de datos
     * @param pedidoEntity el pedido a registrar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se ha registrado, o {@code false} si ya existe ese registro o se ha producido un error al registrarlo
     */
    Mono<Pedido> register(Pedido Pedido);

    /**
     * Actualiza el pedido de la base de datos
     * @param pedidoEntity el pedido a actualizar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se ha actualizado el registro, o {@code false} si ese registro no existe o se ha producido un error al actualizarlo
     */
    Mono<Boolean> update(Pedido Pedido);

    /**
     * Borrar un pedido de la base de datos segun su Id
     * @param id Id del pedido a borrar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se ha borrado el registro, o {@code false} si ese registro no existe o se ha producido un error al borrarlo
     */
    Mono<Boolean> deletePedidoById(String id);

    /**
     * Obtener todos los pedidos en un cierto estado
     * @param estado Estado del pedido
     * @see org.dam2.bomboplats.api.Pedido.Estado
     * @return {@link Flux}<{@link PedidoEntity}> con todos los pedidos que se encuentran en ese estado. {@link Flux#empty()} si no se encuentra ninguno
     */
    Flux<Pedido> findByEstado(Pedido.Estado estado);

    /**
     * Obtener todos los pedidos de un usuario según su Id
     * @param userId Id del usuario
     * @return {@link Flux}<{@link PedidoEntity}> con todos los pedidos del usuario. {@link Flux#empty()} si no se encuentra ninguno
     */
    Flux<Pedido> findByUserId(String userId);

    /**
     * Obtener todos los pedidos de plato según su Id
     * @param platoId Id del plato
     * @return {@link Flux}<{@link PedidoEntity}> con todos los pedidos que se han hecho de ese plato. {@link Flux#empty()} si no se encuentra ninguno
     */
    Flux<Pedido> findByPlatoId(String platoId);

    /**
     * Obtener todos los Ids de los pedidos
     * @return {@link Flux}<{@link String}> con todos los ids de los pedidos
     */
    Flux<String> getIDs();

    /**
     * Comprobar si existe un pedido asignado a un usuario
     * @param idUser Id del usuario
     * @return {@link Mono}<{@link Boolean}> con {@code true} si existe un pedido de ese usuario, o {@code false} si no
     */
    Mono<Boolean> existsByUserId(String idUser);

    /**
     * Comprobar si existe un pedido asignado a un plato
     * @param idPlato Id del plato
     * @return {@link Mono}<{@link Boolean}> con {@code true} si existe un pedido de ese plato, o {@code false} si no
     */
    Mono<Boolean> existsByPlatoId(String idPlato);
}
