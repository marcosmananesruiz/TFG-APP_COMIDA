package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import org.dam2.bomboplatsserver.modelo.mapper.PedidoEntityMapper;
import org.dam2.bomboplatsserver.repo.PedidoRepository;
import org.dam2.bomboplatsserver.service.IPedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PedidoServiceImpl implements IPedidoService {

    @Autowired private PedidoRepository repo;
    @Autowired private R2dbcEntityTemplate template;
    @Autowired private PedidoEntityMapper mapper;

    public static Logger LOGGER = LoggerFactory.getLogger(PedidoServiceImpl.class);
    private final String UNIQUE_CHAR = "P";

    @Override
    public Mono<Pedido> findById(String id) {
        return this.mapper.unmap(this.repo.findById(id));
    }

    @Override
    public Flux<Pedido> findAll() {
        return this.mapper.mapFlux(this.repo.findAll());
    }

    @Override
    public Mono<Pedido> register(Pedido pedido) {
        return this.repo.getNextID()
                .map(idNumber -> this.UNIQUE_CHAR + idNumber)
                .flatMap(id -> {

                    PedidoEntity pedidoEntity = PedidoEntity.builder()
                            .id(id)
                            .idUser(pedido.getUser().getId())
                            .idPlato(pedido.getPlato().getId())
                            .entrega(pedido.getEntrega())
                            .modificaciones(pedido.getModifications().toArray(new String[0]))
                            .estado(pedido.getEstado())
                            .build();

                    return this.template.insert(PedidoEntity.class).using(pedidoEntity)
                            .then(this.mapper.unmap(Mono.just(pedidoEntity)));
                })
                .onErrorResume(DuplicateKeyException.class, e -> {
                    LOGGER.error("{}: Se ha intentado registrar un pedido con ID {}, el cual ya existe", e.getMessage(), pedido.getId());
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    @Override
    public Mono<Boolean> update(Pedido pedido) {
        return this.repo.findById(pedido.getId())
                .flatMap(pedidoEntity -> {

                    pedidoEntity.setIdUser(pedido.getUser().getId());
                    pedidoEntity.setIdPlato(pedido.getPlato().getId());
                    pedidoEntity.setEntrega(pedido.getEntrega());
                    pedidoEntity.setModificaciones(pedido.getModifications().toArray(new String[0]));
                    pedidoEntity.setEstado(pedido.getEstado());

                    return this.repo.save(pedidoEntity);
                })
                .thenReturn(true)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<Boolean> deletePedidoById(String id) {
        return this.repo.findById(id)
                .flatMap(exists -> this.repo.deleteById(id)
                       .doOnSuccess(deletedId -> LOGGER.info("Pedido con ID {} eliminado", deletedId))
                       .thenReturn(true)
                );
    }

    @Override
    public Flux<Pedido> findByEstado(Pedido.Estado estado) {
        return this.mapper.mapFlux(this.repo.findPedidoEntityByEstado(estado))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<Pedido> findByUserId(String userId) {
        return this.mapper.mapFlux(this.repo.findByIdUser(userId))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<Pedido> findByPlatoId(String platoId) {
        return this.mapper.mapFlux(this.repo.findByIdPlato(platoId))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<String> getIDs() {
        return this.repo.getIDs();
    }

    @Override
    public Mono<Boolean> existsByUserId(String idUser) {
        return this.repo.existsByIdUser(idUser);
    }

    @Override
    public Mono<Boolean> existsByPlatoId(String idPlato) {
        return this.repo.existsByIdPlato(idPlato);
    }


}
