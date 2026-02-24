package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplats.api.User;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.repo.UserRepository;
import org.dam2.bomboplatsserver.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired private UserRepository repo;
    @Autowired private R2dbcEntityTemplate template;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final String UNIQUE_CHAR = "U";

    @Override
    public Mono<UserEntity> findByID(String id) {
        return this.repo.findById(id);
    }

    @Override
    public Flux<UserEntity> findAll() {
        return this.repo.findAll();
    }

    @Override
    public Mono<Boolean> register(UserEntity userEntity) {

        // existsById() ya no devuelve un booleano, devuelve un Mono<Boolean> del cual no se puede sacar
        // su valor individual. Por lo que se hace esto

        return this.repo.getNextID()
                .map(idNumber -> this.UNIQUE_CHAR + idNumber)
                .flatMap(id -> {
                    LOGGER.info("nickname antes de insertar {}", userEntity.getNickname());
                    userEntity.setId(id);
                    return this.template.insert(UserEntity.class).using(userEntity);
                }).thenReturn(true)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    LOGGER.error("{}: Se ha intentado registrar un pedido con ID {}, el cual ya existe", e.getMessage(), userEntity.getId());
                    return Mono.just(false);
                });

    }

    @Override
    public Mono<Boolean> update(UserEntity userEntity) {
        return this.repo.findById(userEntity.getId())
                .flatMap(existing -> this.repo.save(userEntity)
                        .doOnNext(updatedEntity -> LOGGER.info("Pedido con ID {} actualizado", updatedEntity.getId()))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> deleteUser(UserEntity userEntity) {
        return this.deleteUserByID(userEntity.getId());
    }

    @Override
    public Mono<Boolean> deleteUserByID(String id) {
        return this.repo.findById(id)
                .flatMap(exists -> this.repo.deleteById(id)
                        .doOnSuccess(deletedId -> LOGGER.info("Pedido con ID {} eliminado", deletedId))
                        .thenReturn(true)
                );
    }

    @Override
    public Mono<String> getPassword(String id) {
        return this.repo.findPasswordById(id);
    }

    @Override
    public Mono<UserEntity> findByEmail(String email) {
        return this.repo.findUserEntityByEmail(email);
    }


}
