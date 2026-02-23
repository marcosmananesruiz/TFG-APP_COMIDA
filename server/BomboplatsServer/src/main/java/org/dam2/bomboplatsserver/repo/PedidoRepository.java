package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PedidoRepository extends ReactiveCrudRepository<PedidoEntity, String> {

    @Query("SELECT nextval('dir_ped')")
    Mono<Long> getNextID();

    Flux<PedidoEntity> findPedidoEntityByEstado(Pedido.Estado estado);
}
