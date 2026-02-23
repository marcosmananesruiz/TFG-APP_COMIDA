package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPedidoService {


    Mono<PedidoEntity> findById(String id);
    Flux<PedidoEntity> findAll();
    Mono<Boolean> register(PedidoEntity pedidoEntity);
    Mono<Boolean> update(PedidoEntity pedidoEntity );
    Mono<Boolean> deletePedidoById(String id);

    Flux<PedidoEntity> findByEstado(Pedido.Estado estado);

}
