package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PedidoRepository extends ReactiveCrudRepository<PedidoEntity, String> {

    /**
     * Obtener el siguiente número para el Id de un pedido
     * @return {@link Mono}<{@link Long}> con el numero del id
     */
    @Query("SELECT nextval('pedido_seq')")
    Mono<Long> getNextID();

    /**
     * Obtener pedidos según su estado
     * @param estado Estado del pedido deseado
     * @return {@link Flux}<{@link PedidoEntity}> con todos los pedidos que se encuentren en ese estado
     */
    Flux<PedidoEntity> findPedidoEntityByEstado(Pedido.Estado estado);

    /**
     * Obtener los pedidos de un usuario
     * @param idUser Id del usuario
     * @return {@link Flux}<{@link PedidoEntity}> con todos los pedidos de ese usuario
     */
    Flux<PedidoEntity> findByIdUser(String idUser);


    /**
     * Obtener los pedidos que se han hecho de un plato
     * @param idPlato Id del plato
     * @return {@link Flux}<{@link PedidoEntity}> con todos los pedidos con ese plato
     */
    Flux<PedidoEntity> findByIdPlato(String idPlato);

    /**
     * Obtener todos los IDs de los pedidos
     * @return {@link Flux}<{@link String}> con todos los ids de los pedidos
     */
    @Query("SELECT id FROM pedidos")
    Flux<String> getIDs();

    /**
     * Comprobar si existe un pedido según el ID del usuario
     * @param idUser Id del usuario
     * @return {@link Mono}<{@link Boolean}> con {@code true} si existe un pedido de ese usuario, {@code false} si no
     */
    Mono<Boolean> existsByIdUser(String idUser);

    /**
     * Comprobar si existe un pedido según el ID del plato
     * @param idPlato Id del plato
     * @return {@link Mono}<{@link Boolean}> con {@code true} si existe un pedido de ese plato, {@code false} si no
     */
    Mono<Boolean> existsByIdPlato(String idPlato);
}
