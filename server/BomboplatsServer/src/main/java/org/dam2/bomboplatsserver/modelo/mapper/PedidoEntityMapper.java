package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PedidoEntityMapper implements EntityMapper<PedidoEntity, Pedido> {



    @Override
    public Mono<PedidoEntity> map(Mono<Pedido> o) {
        return o.map(pedido -> PedidoEntity.builder()
                .id(pedido.getId())
                .idPlato(pedido.getPlato().getId())
                .idUser(pedido.getUser().getId())
                .estado(pedido.getEstado())
                .modificaciones(pedido.getModifications().toArray(new String[0]))
                .entrega(pedido.getEntrega())
                .build());
    }

    // PARA QUE ESTOS DOS FUNCIONEN NECESITO EL PlatoService


    @Override
    public Mono<Pedido> unmap(Mono<PedidoEntity> o) {
        return null;
    }

    @Override
    public Flux<Pedido> mapFlux(Flux<PedidoEntity> o) {
        return null;
    }
}
