package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DireccionEntityMapper implements EntityMapper<DireccionEntity, Direccion> {
    @Override
    public Mono<DireccionEntity> map(Mono<Direccion> o) {
        return null;
    }

    @Override
    public Mono<Direccion> unmap(Mono<DireccionEntity> o) {
        return null;
    }

    public Flux<Direccion> mapFlux(Flux<DireccionEntity> entities) {
        return entities.map(direccionEntity -> Direccion.builder()
                .poblacion(direccionEntity.getPoblacion())
                .calle(direccionEntity.getCalle())
                .codigoPostal(direccionEntity.getCodigoPostal())
                .portal(direccionEntity.getPortal())
                .piso(direccionEntity.getPiso())
                .build()
        );
    }
}
