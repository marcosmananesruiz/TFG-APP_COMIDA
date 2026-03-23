package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.User;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.dam2.bomboplatsserver.service.IPlatoFavoritosService;
import org.dam2.bomboplatsserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

public class UserEntityMapper implements EntityMapper<UserEntity, User> {

    @Autowired private IUserService userService;
    @Autowired private IDireccionService direccionService;
    @Autowired private IPlatoFavoritosService platoFavoritosService;
    @Autowired private DireccionEntityMapper direccionMapper;
    @Autowired private PlatoEntityMapper platoEntityMapper;

    @Override
    public Mono<UserEntity> map(Mono<User> o) {
        return o.flatMap(user ->
                this.userService.getPassword(user.getId())
                        .map(password -> {
                            UserEntity userEntity = new UserEntity();
                            userEntity.setId(user.getId());
                            userEntity.setNickname(user.getNickname());
                            userEntity.setEmail(user.getEmail());
                            userEntity.setPassword(password);
                            userEntity.setIconUrl(user.getIconUrl());
                            return userEntity;
                        })
        );
    }

    @Override
    public Mono<User> unmap(Mono<UserEntity> o) {
        return o.flatMap(userEntity -> {
            Mono<Set<Direccion>> direccionesMono = this.direccionMapper.mapFlux(this.direccionService.getDireccionesOfUser(userEntity)).collect(Collectors.toSet());

            Mono<Set<Plato>> favoritosMono = this.platoEntityMapper.mapFlux(this.platoFavoritosService.getPlatosFavoritosOf(userEntity.getId())).collect(Collectors.toSet());

            return Mono.zip(direccionesMono, favoritosMono)
                    .map(tuple -> User.builder()
                            .id(userEntity.getId())
                            .nickname(userEntity.getNickname())
                            .email(userEntity.getEmail())
                            .iconUrl(userEntity.getIconUrl())
                            .direcciones(tuple.getT1())
                            .platosFavoritos(tuple.getT2())
                            .build()
                    );
        });
    }

    @Override
    public Flux<User> mapFlux(Flux<UserEntity> o) {
        return o.flatMap(userEntity -> {
            Mono<Set<Direccion>> direccionesMono = this.direccionMapper.mapFlux(this.direccionService.getDireccionesOfUser(userEntity)).collect(Collectors.toSet());

            Mono<Set<Plato>> favoritosMono = this.platoEntityMapper.mapFlux(this.platoFavoritosService.getPlatosFavoritosOf(userEntity.getId())).collect(Collectors.toSet());

            return Mono.zip(direccionesMono, favoritosMono)
                    .map(tuple -> User.builder()
                            .id(userEntity.getId())
                            .nickname(userEntity.getNickname())
                            .email(userEntity.getEmail())
                            .iconUrl(userEntity.getIconUrl())
                            .direcciones(tuple.getT1())
                            .platosFavoritos(tuple.getT2())
                            .build()
                    );
        });
    }
}
