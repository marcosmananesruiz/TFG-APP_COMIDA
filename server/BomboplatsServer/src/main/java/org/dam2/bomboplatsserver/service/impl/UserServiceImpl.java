package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.User;
import org.dam2.bomboplats.api.login.LoginAttempt;
import org.dam2.bomboplats.api.login.UserRegister;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.modelo.mapper.DireccionEntityMapper;
import org.dam2.bomboplatsserver.modelo.mapper.PlatoEntityMapper;
import org.dam2.bomboplatsserver.modelo.mapper.UserEntityMapper;
import org.dam2.bomboplatsserver.repo.UserRepository;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.dam2.bomboplatsserver.service.IPedidoService;
import org.dam2.bomboplatsserver.service.IPlatoFavoritosService;
import org.dam2.bomboplatsserver.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired private UserRepository repo;
    @Autowired private R2dbcEntityTemplate template;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private UserEntityMapper mapper;

    @Autowired private IDireccionService direccionService;
    @Autowired private IPlatoFavoritosService platoFavoritosService;
    @Lazy @Autowired private IPedidoService pedidoService;

    private final String UNIQUE_CHAR = "U";
    private final String DEFAULT_ICON = "profile/default.jpg";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Mono<User> findByID(String id) {
        return this.mapper.unmap(this.repo.findById(id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<User> findAll() {
        return this.mapper.mapFlux(this.repo.findAll())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<User> register(UserRegister register) {
        return this.repo.existsByEmail(register.email())
                .flatMap(existeEmail -> {

                    // Si existe ya un email registrado, devolvemos 500
                    if (existeEmail) {
                        LOGGER.error("Se ha intentado registrar un usuario con email {}, el cual ya existe", register.email());
                        return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
                    }

                    // Crear la entidad para la BBDD
                    UserEntity userEntity = new UserEntity("U0", register.nickname(), register.email(), "", this.DEFAULT_ICON);
                    userEntity.setPassword(this.passwordEncoder.encode(register.password()));

                    return this.repo.getNextID() // ID Custom con el Sequence de PostgreSQL
                            .map(idNumber -> this.UNIQUE_CHAR + idNumber)
                            .flatMap(id -> {
                                userEntity.setId(id);

                                // Utilizo template para que haga correctamente el insert con el ID ya puesto.
                                // Si no lo hiciera, como ya tiene un ID se pensará que es un Update y fallará
                                return this.template.insert(UserEntity.class).using(userEntity);

                            }).then(this.mapper.unmap(Mono.just(userEntity))); // Devolvemos el dto
                }).onErrorResume(DuplicateKeyException.class, e -> {
                    // Si por alguna razon se repite la clave pues tiramos un 500
                    LOGGER.error("{}: Se ha intentado registrar un usuario con id que ya existe", e.getMessage());
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    @Override
    public Mono<Boolean> update(User user) {
        return this.repo.findById(user.getId())
                .flatMap(userEntity -> {

                    // Actualizamos los datos de la entidad para que no guarde literalmente la misma
                    userEntity.setEmail(user.getEmail());
                    userEntity.setNickname(user.getNickname());
                    userEntity.setIconUrl(user.getIconUrl());

                    Mono<Void> direccionesMono = syncDirecciones(user); // Sincronizamos las direcciones
                    Mono<Void> platosFavoritos = syncPlatosFavoritos(user); // Y los platos favoritos
                    // Para añadir los nuevos y borrar los que ya no están

                    return Mono.just(userEntity).flatMap(entity -> Mono.when(direccionesMono, platosFavoritos))
                            .then(this.repo.save(userEntity));
                })
                .thenReturn(true) // Devolver true para indicar que ha salido bien
                .defaultIfEmpty(false); // Falso para indicar que ese usuario no esta en la base de datos
    }

    @Override
    public Mono<Boolean> deleteUser(User user) {
        return this.deleteUserByID(user.getId());
    }

    @Override
    public Mono<Boolean> deleteUserByID(String id) {
        return this.repo.findById(id).flatMap(userEntity -> {
            Mono<Void> clearDirecciones = clearDirecciones(id);
            Mono<Void> clearPlatos = clearPlatosFavoritos(id);
            Mono<Void> clearPedidos = clearPedidos(id);
            return Mono.when(clearDirecciones, clearPlatos, clearPedidos).then(this.repo.delete(userEntity));
        })
        .thenReturn(true).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<String> getPassword(String id) {
        return this.repo.findPasswordById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return this.mapper.unmap(this.repo.findUserEntityByEmail(email))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<Boolean> verifyLogin(LoginAttempt loginAttempt) {
        return this.repo.findUserEntityByEmail(loginAttempt.email())
                .map(userEntity -> this.passwordEncoder.matches(loginAttempt.password(), userEntity.getPassword()))
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> updatePassword(String userId, String newPassword) {
        return this.repo.findById(userId).flatMap(userEntity -> {
            userEntity.setPassword(this.passwordEncoder.encode(newPassword));
            return this.repo.save(userEntity);
        }).thenReturn(true)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<Plato> getPlatosFavoritos(String userId) {
        return this.platoFavoritosService.getPlatosFavoritosOf(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<String> getUserIds() {
        return this.repo.getIDs();
    }

    private Mono<Void> syncDirecciones(User user) {
        return this.direccionService.getDireccionesOfUser(user.getId())
                .collectList()
                .flatMap(direccionesExistentes -> {

                    // Los IDs de las direcciones que tiene el usuario que pasan
                    Set<String> nuevosIds = user.getDirecciones().stream()
                            .map(Direccion::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    // Sacamos todas las direcciones guardadas del usuario y sacamos las que aun se mantienen
                    Set<String> idsAEliminar = direccionesExistentes.stream()
                            .map(Direccion::getId)
                            .collect(Collectors.toSet());
                    idsAEliminar.removeAll(nuevosIds);

                    // Borrado
                    Mono<Void> eliminaciones = Flux.fromIterable(idsAEliminar)
                            .flatMap(id -> this.direccionService.deleteDireccionByID(id))
                            .then();

                    // Actualizamos todas las direcciones para que tengan el userId
                    Mono<Void> actualizaciones = Flux.fromIterable(user.getDirecciones())
                            .flatMap(direccion -> this.direccionService.asignarUserId(direccion, user.getId()))
                            .then();

                    return Mono.when(eliminaciones, actualizaciones);
                });
    }

    private Mono<Void> clearDirecciones(String id) {
        return this.findByID(id).flatMap(user -> {
            user.getDirecciones().clear();
            return this.update(user);
        }).then();
    }

    private Mono<Void> syncPlatosFavoritos(User user) {
        String userId = user.getId();

        return this.platoFavoritosService.getPlatosFavoritosOf(userId)
                .map(Plato::getId)
                .collect(Collectors.toSet())
                .flatMap(actualesIds -> {

                    Set<String> nuevosIds = user.getPlatosFavoritos()
                            .stream()
                            .map(Plato::getId)
                            .collect(Collectors.toSet());

                    Set<String> aInsertar = new HashSet<>(nuevosIds);
                    aInsertar.removeAll(actualesIds);

                    Set<String> aEliminar = new HashSet<>(actualesIds);
                    aEliminar.removeAll(nuevosIds);

                    Mono<Void> insertMono = Flux.fromIterable(aInsertar)
                            .flatMap(idPlato -> {
                                return this.platoFavoritosService.asignarFavorito(idPlato, userId);
                            })
                            .then();

                    Mono<Void> deleteMono = Flux.fromIterable(aEliminar)
                            .flatMap(idPlato -> this.platoFavoritosService.deleteByUserIdAndPlatoId(userId, idPlato))
                            .then();

                    return Mono.when(insertMono, deleteMono);
                });
    }

    private Mono<Void> clearPlatosFavoritos(String id) {
        return this.findByID(id).flatMap(user -> {
            user.getPlatosFavoritos().clear();
            return this.update(user);
        }).then();
    }

    private Mono<Void> clearPedidos(String id) {
        return this.pedidoService.existsByUserId(id).flatMap(exists -> {
            if (exists) {
                return this.pedidoService.findByUserId(id).flatMap(pedido -> this.pedidoService.deletePedidoById(pedido.getId())).then();
            }
            return Mono.empty();
        }).then();
    }


}
