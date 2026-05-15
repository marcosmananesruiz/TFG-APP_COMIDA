package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.User;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.repo.UserRepository;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.dam2.bomboplatsserver.service.IPlatoFavoritosService;
import org.dam2.bomboplatsserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper encargado de convertir entre {@link User} (modelo de API)
 * y {@link UserEntity} (entidad de base de datos).
 * Al construir el DTO, recupera de forma reactiva las direcciones
 * y platos favoritos asociados al usuario.
 */
public class UserEntityMapper implements EntityMapper<UserEntity, User> {

    @Autowired private UserRepository repo;
    @Autowired private IDireccionService direccionService;
    @Autowired private IPlatoFavoritosService platoFavoritosService;

    /**
     * Convierte un {@link User} a su representación como entidad de base de datos.
     * Recupera la contraseña almacenada en base de datos para no perderla,
     * ya que el DTO no la expone.
     *
     * @param o Mono con el usuario a convertir
     * @return Mono con la entidad resultante
     */
    @Override
    public Mono<UserEntity> map(Mono<User> o) {
        return o.flatMap(user ->
                this.repo.findPasswordById(user.getId())
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

    /**
     * Convierte una {@link UserEntity} a su representación como modelo de API.
     * Recupera en paralelo las direcciones y platos favoritos del usuario
     * antes de construir el DTO final.
     *
     * @param o Mono con la entidad a convertir
     * @return Mono con el usuario completo incluyendo direcciones y favoritos
     */
    @Override
    public Mono<User> unmap(Mono<UserEntity> o) {
        return o.flatMap(userEntity -> {
            Mono<Set<Direccion>> direccionesMono = this.direccionService.getDireccionesOfUser(userEntity).collect(Collectors.toSet());

            Mono<Set<Plato>> favoritosMono = this.platoFavoritosService.getPlatosFavoritosOf(userEntity.getId()).collect(Collectors.toSet());

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

    /**
     * Convierte un flujo de entidades {@link UserEntity} a un flujo de objetos {@link User}.
     * Para cada entidad, recupera en paralelo sus direcciones y platos favoritos.
     *
     * @param o Flux con las entidades a convertir
     * @return Flux con los usuarios completos incluyendo direcciones y favoritos
     */
    @Override
    public Flux<User> mapFlux(Flux<UserEntity> o) {
        return o.flatMap(userEntity -> {
            Mono<Set<Direccion>> direccionesMono = this.direccionService.getDireccionesOfUser(userEntity.getId()).collect(Collectors.toSet());

            Mono<Set<Plato>> favoritosMono = this.platoFavoritosService.getPlatosFavoritosOf(userEntity.getId()).collect(Collectors.toSet());

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
