package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.repo.DireccionRepository;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DireccionServiceImpl implements IDireccionService {

    @Autowired private DireccionRepository repo;
    @Autowired private R2dbcEntityTemplate template;

    public static final Logger LOGGER = LoggerFactory.getLogger(DireccionServiceImpl.class);

    private final String UNIQUE_CHAR = "D";

    @Override
    public Mono<DireccionEntity> findById(String id) {
        return this.repo.findById(id);
    }

    @Override
    public Flux<DireccionEntity> findAll() {
        return this.repo.findAll();
    }

    @Override
    public Mono<Boolean> register(DireccionEntity direccionEntity) {
        return this.repo.getNextID()
                .map(idNumber -> this.UNIQUE_CHAR + idNumber)
                .flatMap(id -> {
                    direccionEntity.setId(id);
                    return this.template.insert(DireccionEntity.class).using(direccionEntity);
                }).thenReturn(true)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    LOGGER.error("{}: Se ha intentado registrar una direccion con ID {}, la cual ya existe", e.getMessage(), direccionEntity.getId());
                   return Mono.just(false);
                });
    }

    @Override
    public Mono<Boolean> update(DireccionEntity direccionEntity) {
        return this.repo.findById(direccionEntity.getId())
                .flatMap(_ -> this.repo.save(direccionEntity)
                        .doOnNext(updatedEntity -> LOGGER.info("Direccion con ID {} actualizada", updatedEntity.getId()))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> deleteDireccion(DireccionEntity direccionEntity) {
        return this.deleteDireccionByID(direccionEntity.getId());
    }

    @Override
    public Mono<Boolean> deleteDireccionByID(String id) {
        return this.repo.findById(id)
                .flatMap(exists -> this.repo.deleteById(id)
                        .doOnNext(deletedId -> LOGGER.info("Direccion con ID {} eliminado", deletedId))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    @Override
    public Mono<String> getIdResidente(String id) {
        return this.repo.findIdResidenteById(id);
    }

    @Override
    public Flux<DireccionEntity> getDireccionesOfUser(UserEntity userEntity) {
        return this.repo.findDireccionByResidentId(userEntity.getId());
    }
}
