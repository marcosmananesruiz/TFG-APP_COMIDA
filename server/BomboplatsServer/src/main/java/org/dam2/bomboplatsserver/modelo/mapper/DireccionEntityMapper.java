package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DireccionEntityMapper implements EntityMapper<DireccionEntity, Direccion> {

    @Autowired private IDireccionService service;

    @Override
    public Mono<DireccionEntity> map(Mono<Direccion> o) {
        return o.flatMap(direccion -> {
                DireccionEntity direccionEntity = new DireccionEntity();
                return this.service.getIdResidente(direccionEntity.getId()).doOnNext(idResidente -> {
                    direccionEntity.setId(direccion.getId());
                    direccionEntity.setPoblacion(direccion.getPoblacion());
                    direccionEntity.setCalle(direccion.getCalle());
                    direccionEntity.setCodigoPostal(direccion.getCodigoPostal());
                    direccionEntity.setPortal(direccion.getPortal());
                    direccionEntity.setPiso(direccion.getPiso());
                    direccionEntity.setIdResidente(idResidente);
                }).thenReturn(direccionEntity);
            }
        );
    }

    @Override
    public Mono<Direccion> unmap(Mono<DireccionEntity> o) {
        return o.map(direccion -> Direccion.builder()
                            .id(direccion.getId())
                            .poblacion(direccion.getPoblacion())
                            .calle(direccion.getCalle())
                            .codigoPostal(direccion.getCodigoPostal())
                            .portal(direccion.getPortal())
                            .piso(direccion.getPiso())
                            .build());
    }

    public Flux<Direccion> mapFlux(Flux<DireccionEntity> entities) {
        return entities.map(direccionEntity -> Direccion.builder()
                .id(direccionEntity.getId())
                .poblacion(direccionEntity.getPoblacion())
                .calle(direccionEntity.getCalle())
                .codigoPostal(direccionEntity.getCodigoPostal())
                .portal(direccionEntity.getPortal())
                .piso(direccionEntity.getPiso())
                .build()
        );
    }
}
