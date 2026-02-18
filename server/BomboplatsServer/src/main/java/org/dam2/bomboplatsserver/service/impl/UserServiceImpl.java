package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.repo.UserRepository;
import org.dam2.bomboplatsserver.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired private UserRepository repo;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final String UNIQUE_CHAR = "U";

    @Override
    public Mono<UserEntity> findByID(String id) {
        return this.repo.findById(id);
    }

    @Override
    public Mono<Boolean> register(UserEntity userEntity) {

        // existsById() ya no devuelve un booleano, devuelve un Mono<Boolean> del cual no se puede sacar
        // su valor individual. Por lo que se hace esto

        return this.repo.existsById(userEntity.getId())
                .flatMap(exists -> { // .map() funcionaria pero puede pasar que el elemento sea una lista, mejor usar flatmap
                    if (!exists) {
                        this.repo.getNextID().doOnNext(idNumber -> {
                            userEntity.setId(this.UNIQUE_CHAR + idNumber);
                            this.repo.save(userEntity).doOnNext(savedEntity -> {
                                // Como quiero mostrar información de la entidad que se acaba de registrar,
                                // me suscribo al observable para que consuma el dato en cuanto llegue
                                LOGGER.info("User saved with id {}", savedEntity.getId());
                            });
                        });
                    }
                    return Mono.just(!exists);
                });
    }

    @Override
    public Mono<Boolean> update(UserEntity userEntity) {
        return this.repo.existsById(userEntity.getId())
                .flatMap(exists -> {
                    if (exists) {
                        this.repo.save(userEntity).doOnNext(updatedEntity -> {
                            LOGGER.info("User with id {} updated", updatedEntity.getId());
                        });
                    }
                    return Mono.just(exists);
                });
    }

    @Override
    public Mono<Boolean> deleteUser(UserEntity userEntity) {
        return this.deleteUserByID(userEntity.getId());
    }

    @Override
    public Mono<Boolean> deleteUserByID(String id) {
        return this.repo.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        this.repo.deleteById(id);
                        LOGGER.info("User with id {} deleted", id);
                    }
                    return Mono.just(exists);
                });
    }

    @Override
    public Mono<String> getPassword(String id) {
        return this.repo.findPasswordById(id);
    }




}
