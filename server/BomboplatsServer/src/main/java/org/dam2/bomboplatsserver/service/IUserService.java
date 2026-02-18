package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import reactor.core.publisher.Mono;


public interface IUserService {

    Mono<UserEntity> findByID(String id);
    Mono<Boolean> register(UserEntity userEntity);
    Mono<Boolean> update(UserEntity userEntity);
    Mono<Boolean> deleteUser(UserEntity userEntity);
    Mono<Boolean> deleteUserByID(String id);
    Mono<String> getPassword(String id);
}
