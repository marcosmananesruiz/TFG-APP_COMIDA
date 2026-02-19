package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.repo.DireccionRepository;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DireccionServiceImpl implements IDireccionService {

    @Autowired private DireccionRepository repo;
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
        return this.repo.existsById(direccionEntity.getId())
                .flatMap(exists -> { // .map() funcionaria pero puede pasar que el elemento sea una lista, mejor usar flatmap
                    if (!exists) {
                        this.repo.getNextID().doOnNext(idNumber -> {
                            direccionEntity.setId(this.UNIQUE_CHAR + idNumber);
                            this.repo.save(direccionEntity).doOnNext(savedEntity -> {
                                // Como quiero mostrar información de la entidad que se acaba de registrar,
                                // me suscribo al observable para que consuma el dato en cuanto llegue
                                LOGGER.info("Direccion saved with id {}", savedEntity.getId());
                            });
                        });
                    }
                    return Mono.just(!exists);
                });
    }

    @Override
    public Mono<Boolean> update(DireccionEntity direccionEntity) {
        return this.repo.existsById(direccionEntity.getId())
                .flatMap(exists -> {
                    if (exists) {
                        this.repo.save(direccionEntity).doOnNext(updatedEntity -> {
                            LOGGER.info("Direccion with id {} updated", updatedEntity.getId());
                        });
                    }
                    return Mono.just(exists);
                });
    }

    @Override
    public Mono<Boolean> deleteDireccion(DireccionEntity direccionEntity) {
        return this.deleteDireccionByID(direccionEntity.getId());
    }

    @Override
    public Mono<Boolean> deleteDireccionByID(String id) {
        return this.repo.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        this.repo.deleteById(id);
                        LOGGER.info("Direccion with id {} deleted", id);
                    }
                    return Mono.just(exists);
                });
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
