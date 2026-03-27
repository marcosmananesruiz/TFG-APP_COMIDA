package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplats.api.Restaurante;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRestauranteService {

    Mono<Restaurante> findById(String id);
    Flux<Restaurante> findAll();
    Mono<Restaurante> register(Restaurante restaurante);
    Mono<Boolean> update(Restaurante restaurante);
    Mono<Boolean> deleteRestauranteById(String id);

    Flux<Restaurante> findByNombre(String nombre);
    Flux<Restaurante> findByNombreContaining(String nombre);
    Flux<Restaurante> findByDescriptionContaining(String description);
    Flux<Restaurante> findByTag(String tag);
}