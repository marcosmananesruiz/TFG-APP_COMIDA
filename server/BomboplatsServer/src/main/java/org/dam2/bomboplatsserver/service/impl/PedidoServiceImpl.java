package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import org.dam2.bomboplatsserver.repo.PedidoRepository;
import org.dam2.bomboplatsserver.service.IPedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PedidoServiceImpl implements IPedidoService {

    @Autowired private PedidoRepository repo;
    @Autowired private R2dbcEntityTemplate template;

    public static Logger LOGGER = LoggerFactory.getLogger(PedidoServiceImpl.class);
    private final String UNIQUE_CHAR = "P";

    @Override
    public Mono<PedidoEntity> findById(String id) {
        return this.repo.findById(id);
    }

    @Override
    public Flux<PedidoEntity> findAll() {
        return this.repo.findAll();
    }

    @Override
    public Mono<Boolean> register(PedidoEntity pedidoEntity) {

        return this.repo.getNextID()
                .map(idNumber -> this.UNIQUE_CHAR + idNumber)
                .flatMap(id -> {
                    pedidoEntity.setId(id);
                    return this.template.insert(PedidoEntity.class).using(pedidoEntity);
                }).thenReturn(true)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    LOGGER.error("{}: Se ha intentado registrar un pedido con ID {}, el cual ya existe", e.getMessage(), pedidoEntity.getId());
                    return Mono.just(false);
                });

    }

    @Override
    public Mono<Boolean> update(PedidoEntity pedidoEntity) {
        return this.repo.findById(pedidoEntity.getId())
                .flatMap(existing -> this.repo.save(pedidoEntity)
                        .doOnNext(updatedEntity -> LOGGER.info("Pedido con ID {} actualizado", updatedEntity.getId()))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> deletePedidoById(String id) {
        return this.repo.findById(id)
                .flatMap(exists -> this.repo.deleteById(id)
                       .doOnNext(deletedId -> LOGGER.info("Pedido con ID {} eliminado", deletedId))
                       .thenReturn(true)
                ).defaultIfEmpty(false);
    }
}
