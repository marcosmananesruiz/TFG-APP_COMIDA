package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.dam2.bomboplatsserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public class PedidoEntityMapper implements EntityMapper<PedidoEntity, Pedido> {

    @Autowired private IUserService userService;
    @Autowired private IPlatoService platoService;
    @Autowired private UserEntityMapper userMapper;
    @Autowired private PlatoEntityMapper platoMapper;

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

    @Override
    public Mono<Pedido> unmap(Mono<PedidoEntity> o) {
        return o.flatMap(pedidoEntity -> {
            Pedido pedido = new Pedido();
            return this.userService.findByID(pedidoEntity.getIdUser()).flatMap(userEntity -> this.userMapper.unmap(Mono.just(userEntity))
                    .flatMap(user -> this.platoService.findById(pedidoEntity.getIdPlato()).flatMap(platoEntity -> this.platoMapper.unmap(Mono.just(platoEntity))
                            .map(plato -> {
                                pedido.setId(pedidoEntity.getId());
                                pedido.setEstado(pedidoEntity.getEstado());
                                pedido.setModifications(Arrays.asList(pedidoEntity.getModificaciones()));
                                pedido.setEntrega(pedidoEntity.getEntrega());
                                pedido.setUser(user);
                                pedido.setPlato(plato);
                                return pedido;
                            })
                    )));
        });
    }

    @Override
    public Flux<Pedido> mapFlux(Flux<PedidoEntity> o) {
        return o.flatMap(pedidoEntity -> {
            Pedido pedido = new Pedido();
            return this.userService.findByID(pedidoEntity.getIdUser()).flatMap(userEntity -> this.userMapper.unmap(Mono.just(userEntity))
                    .flatMap(user -> this.platoService.findById(pedidoEntity.getIdPlato()).flatMap(platoEntity -> this.platoMapper.unmap(Mono.just(platoEntity))
                            .map(plato -> {
                                pedido.setId(pedidoEntity.getId());
                                pedido.setEstado(pedidoEntity.getEstado());
                                pedido.setModifications(Arrays.asList(pedidoEntity.getModificaciones()));
                                pedido.setEntrega(pedidoEntity.getEntrega());
                                pedido.setUser(user);
                                pedido.setPlato(plato);
                                return pedido;
                            })
                    )));
        });
    }
}
