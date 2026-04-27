package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.dam2.bomboplatsserver.modelo.entity.UserEntity;
import org.dam2.bomboplatsserver.modelo.mapper.DireccionEntityMapper;
import org.dam2.bomboplatsserver.repo.DireccionRepository;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.dam2.bomboplatsserver.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DireccionServiceImpl implements IDireccionService {

    @Autowired private DireccionRepository repo;
    @Autowired private R2dbcEntityTemplate template;
    @Autowired private DireccionEntityMapper mapper;

    @Lazy
    @Autowired
    private IUserService userService;

    public static final Logger LOGGER = LoggerFactory.getLogger(DireccionServiceImpl.class);

    private final String UNIQUE_CHAR = "D";

    @Override
    public Mono<Direccion> findById(String id) {
        return this.mapper.unmap(this.repo.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))));
    }

    @Override
    public Flux<Direccion> findAll() {
        return this.mapper.mapFlux(this.repo.findAll())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<Direccion> register(Direccion direccion) {
        return this.repo.getNextID()
                .map(idNumber -> this.UNIQUE_CHAR + idNumber)
                .flatMap(id -> {

                    DireccionEntity direccionEntity = DireccionEntity.builder()
                            .poblacion(direccion.getPoblacion())
                            .codigoPostal(direccion.getCodigoPostal())
                            .calle(direccion.getCalle())
                            .portal(direccion.getPortal())
                            .piso(direccion.getPiso())
                            .build();

                    direccionEntity.setId(id);
                    return this.template.insert(DireccionEntity.class).using(direccionEntity)
                            .then(this.mapper.unmap(Mono.just(direccionEntity)));
                })
                .onErrorResume(DuplicateKeyException.class, e -> {
                    LOGGER.error("{}: Se ha intentado registrar una direccion con ID {}, la cual ya existe", e.getMessage(), direccion.getId());
                   return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    @Override
    public Mono<Boolean> update(Direccion direccion) {
        return this.repo.findById(direccion.getId())
                .flatMap(direccionEntity -> {

                    // Actualizar los datos de la entidad
                    direccionEntity.setPoblacion(direccion.getPoblacion());
                    direccionEntity.setCodigoPostal(direccion.getCodigoPostal());
                    direccionEntity.setCalle(direccion.getCalle());
                    direccionEntity.setPiso(direccion.getPiso());
                    direccionEntity.setPortal(direccion.getPortal());
                    // Ignoro los userId y restauranteId porque no vienen en Direccion.
                    // Para ello están los métodos asignarUser y asignarRestaurante

                    return this.repo.save(direccionEntity);
                })
                .thenReturn(true)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<Boolean> deleteDireccion(Direccion direccion) {
        return this.deleteDireccionByID(direccion.getId());
    }

    @Override
    public Mono<Boolean> deleteDireccionByID(String id) {
        return this.repo.findById(id)
                .flatMap(direccionEntity -> {
                    LOGGER.info("deleteDireccionByID id={}", id);
                    Mono<Void> clearUsers = clearDireccionUser(id);
                    return Mono.when(clearUsers).then(this.repo.delete(direccionEntity));
                }).thenReturn(true).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<String> getUserID(String id) {
        return this.repo.findIdUserById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }
    @Override
    public Mono<String> getRestauranteID(String id) {
        return this.repo.findIdRestauranteById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<Direccion> getDireccionesOfUser(UserEntity userEntity) {
        return getDireccionesOfUser(userEntity.getId());
    }

    @Override
    public Flux<Direccion> getDireccionesOfRestaurante(RestauranteEntity restauranteEntity) {
        return this.getDireccionesOfRestaurante(restauranteEntity.getId());
    }

    @Override
    public Flux<Direccion> getDireccionesOfUser(String userID) {
        return this.mapper.mapFlux(this.repo.findByIdUser(userID));
    }
    // A estos dos no les pongo que devuelvan 404 porque sí que es posible que no tengan direcciones al principio
    @Override
    public Flux<Direccion> getDireccionesOfRestaurante(String restauranteID) {
        return this.mapper.mapFlux(this.repo.findByIdRestaurante(restauranteID));
    }

    @Override
    public Mono<Boolean> asignarUserId(Direccion direccion, String userId) {

        if (direccion == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        }

        return this.repo.findById(direccion.getId()).flatMap(direccionEntity -> {
            direccionEntity.setIdUser(userId);
            return this.repo.save(direccionEntity);
        }).switchIfEmpty(this.register(direccion)
                .flatMap(dir -> this.mapper.map(Mono.just(dir)))
                .flatMap(direccionEntity -> {
                    direccionEntity.setIdUser(userId);
                    return this.repo.save(direccionEntity);
                })).thenReturn(true);
    }

    @Override
    public Mono<Boolean> asignarRestauranteId(Direccion direccion, String restauranteId) {
        return this.repo.findById(direccion.getId()).flatMap(direccionEntity -> {
                    direccionEntity.setIdRestaurante(restauranteId);
                    return this.repo.save(direccionEntity);
        }).thenReturn(true)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<String> getIDs() {
        return this.repo.getIDs();
    }

    private Mono<Void> clearDireccionUser(String id) {
        return this.getUserID(id).flatMap(userId -> {
                    LOGGER.info("clearDireccionUser id={}", userId);
                    return this.userService.findByID(userId);
                })
                .flatMap(user -> this.findById(id).flatMap(direccion -> {
                    user.getDirecciones().remove(direccion);
                    return this.userService.saveUserEntity(user);
                })).then();
    }
}
