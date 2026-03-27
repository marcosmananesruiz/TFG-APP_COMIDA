package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.User;
import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.dam2.bomboplatsserver.modelo.entity.PlatoFavoritosEntity;
import org.dam2.bomboplatsserver.repo.PlatoFavoritosRepository;
import org.dam2.bomboplatsserver.service.IPlatoFavoritosService;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PlatoFavoritosServiceImpl implements IPlatoFavoritosService {

    @Autowired private PlatoFavoritosRepository repo;
    @Autowired private IPlatoService platoService;

    @Override
    public Flux<Plato> getPlatosFavoritosOf(String userId) {
        return this.repo.findByUserId(userId).flatMap(platoFavoritosEntity -> this.platoService.findById(platoFavoritosEntity.getPlatoId()));
    }

    @Override
    public Flux<PlatoFavoritosEntity> findAll() {
        return this.repo.findAll();
    }

    @Override
    public Mono<Boolean> register(PlatoFavoritosEntity platoFavoritosEntity) {
        return this.repo.save(platoFavoritosEntity)
                .thenReturn(true)
                .onErrorResume(DuplicateKeyException.class, e -> Mono.just(false));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return this.repo.deleteById(id).thenReturn(true);
    }

    @Override
    public Mono<Void> deleteByUserIdAndPlatoId(String userId, String platoId) {
        return this.repo.deleteByUserIdAndPlatoId(userId, platoId);
    }

    @Override
    public Mono<Boolean> asignarFavorito(Plato plato, String userId) {
        return this.asignarFavorito(plato.getId(), userId);
    }

    @Override
    public Mono<Boolean> asignarFavorito(Plato plato, User user) {
        return this.asignarFavorito(plato.getId(), user.getId());
    }

    @Override
    public Mono<Boolean> asignarFavorito(String platoId, User user) {
        return this.asignarFavorito(platoId, user.getId());
    }

    @Override
    public Mono<Boolean> asignarFavorito(String platoId, String userId) {
        return this.register(new PlatoFavoritosEntity(null, userId, platoId));
    }
}
