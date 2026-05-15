package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.dam2.bomboplatsserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;



/**
 * Mapper encargado de convertir entre {@link Pedido} (modelo de API)
 * y {@link PedidoEntity} (entidad de base de datos).
 * Al construir el DTO, recupera el usuario y el plato asociados
 * a partir de sus respectivos IDs almacenados en la entidad.
 */
public class PedidoEntityMapper implements EntityMapper<PedidoEntity, Pedido> {

    @Autowired private IUserService userService;
    @Autowired private IPlatoService platoService;

    /**
     * Convierte un {@link Pedido} a su representación como entidad de base de datos.
     * Solo almacena los IDs del usuario y del plato, no los objetos completos.
     *
     * @param o Mono con el pedido a convertir
     * @return Mono con la entidad resultante
     */
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

    /**
     * Convierte una {@link PedidoEntity} a su representación como modelo de API.
     * Recupera el usuario y el plato completos a partir de sus IDs
     * antes de construir el DTO final.
     *
     * @param o Mono con la entidad a convertir
     * @return Mono con el pedido completo incluyendo usuario y plato
     */
    @Override
    public Mono<Pedido> unmap(Mono<PedidoEntity> o) {
        return o.flatMap(pedidoEntity -> {
            Pedido pedido = new Pedido();
            return this.userService.findByID(pedidoEntity.getIdUser()).flatMap(user -> this.platoService.findById(pedidoEntity.getIdPlato())
                    .map(plato -> {
                        pedido.setId(pedidoEntity.getId());
                        pedido.setEstado(pedidoEntity.getEstado());
                        pedido.setModifications(Arrays.asList(pedidoEntity.getModificaciones()));
                        pedido.setEntrega(pedidoEntity.getEntrega());
                        pedido.setUser(user);
                        pedido.setPlato(plato);
                        return pedido;
                    })
            );

        });
    }

    /**
     * Convierte un flujo de entidades {@link PedidoEntity} a un flujo de objetos {@link Pedido}.
     * Para cada entidad, recupera el usuario y el plato completos a partir de sus IDs.
     *
     * @param o Flux con las entidades a convertir
     * @return Flux con los pedidos completos incluyendo usuario y plato
     */
    @Override
    public Flux<Pedido> mapFlux(Flux<PedidoEntity> o) {
        return o.flatMap(pedidoEntity -> {
            Pedido pedido = new Pedido();
            return this.userService.findByID(pedidoEntity.getIdUser()).flatMap(user -> this.platoService.findById(pedidoEntity.getIdPlato())
                    .map(plato -> {
                        pedido.setId(pedidoEntity.getId());
                        pedido.setEstado(pedidoEntity.getEstado());
                        pedido.setModifications(Arrays.asList(pedidoEntity.getModificaciones()));
                        pedido.setEntrega(pedidoEntity.getEntrega());
                        pedido.setUser(user);
                        pedido.setPlato(plato);
                        return pedido;
                    })
            );

        });
    }
}
