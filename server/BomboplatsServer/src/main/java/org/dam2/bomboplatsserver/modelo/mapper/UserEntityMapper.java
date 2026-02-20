package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.User;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.dam2.bomboplatsserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class UserEntityMapper implements EntityMapper<UserEntity, User> {

    @Autowired private IUserService userService;
    @Autowired private IDireccionService direccionService;
    @Autowired private DireccionEntityMapper direccionMapper;

    @Override
    public Mono<UserEntity> map(Mono<User> o) {
        return o.flatMap(user -> {
            UserEntity userEntity = new UserEntity();
            return this.userService.getPassword(user.getId())
                    .doOnNext(password -> {
                        userEntity.setId(user.getId());
                        userEntity.setNickname(user.getNickname());
                        userEntity.setEmail(user.getEmail());
                        userEntity.setPassword(password);
                        userEntity.setIconUrl(user.getIconUrl());
                    }).thenReturn(userEntity);
        });
    }

    @Override
    public Mono<User> unmap(Mono<UserEntity> o) {
        return o.flatMap(userEntity -> this.direccionMapper.mapFlux(this.direccionService.getDireccionesOfUser(userEntity))
                .collect(Collectors.toSet())
                .map(direcciones -> User.builder()
                        .id(userEntity.getId())
                        .nickname(userEntity.getNickname())
                        .email(userEntity.getEmail())
                        .iconUrl(userEntity.getIconUrl())
                        .direcciones(direcciones)
                        .build()));
    }

    @Override
    public Flux<User> mapFlux(Flux<UserEntity> o) {
        return o.flatMap(userEntity -> this.direccionMapper.mapFlux(this.direccionService.getDireccionesOfUser(userEntity))
                .collect(Collectors.toSet())
                .map(direcciones -> User.builder()
                        .id(userEntity.getId())
                        .nickname(userEntity.getNickname())
                        .email(userEntity.getEmail())
                        .iconUrl(userEntity.getIconUrl())
                        .direcciones(direcciones)
                        .build()));
    }
}
