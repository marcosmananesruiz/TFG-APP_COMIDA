package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplats.api.Plato;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlatoService {

    Mono<Plato> findById(String id);
    Flux<Plato> findAll();
    Mono<Plato> register(Plato plato);
    Mono<Boolean> update(Plato plato);
    Mono<Boolean> deletePlatoById(String id);

    Flux<Plato> findByIdRestaurante(String idRestaurante);
    Flux<Plato> findByNombreContaining(String nombre);
    Flux<Plato> findByIdRestauranteAndNombreContaining(String idRestaurante, String nombre);
    Flux<Plato> findByTag(String tag);
    Mono<Plato> registerConRestaurante(Plato plato, String idRestaurante);
}