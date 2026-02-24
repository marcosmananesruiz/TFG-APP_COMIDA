package org.dam2.bomboplatsserver.controller;

import org.dam2.bomboplats.api.Restaurante;
import org.dam2.bomboplatsserver.modelo.mapper.RestauranteEntityMapper;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.dam2.bomboplatsserver.service.IRestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/restaurante")
public class RestauranteController {

    @Autowired private IRestauranteService service;
    @Autowired private RestauranteEntityMapper mapper;

    @GetMapping("/get")
    public Flux<Restaurante> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }


    @GetMapping("/get/{id}")
    public Mono<Restaurante> getRestauranteById(@PathVariable String id) {
        return this.mapper.unmap(this.service.findById(id));
    }

    @PostMapping("/register")
    public Mono<Boolean> register(@RequestBody Restaurante restaurante) {
        return this.mapper.map(Mono.just(restaurante))
                .flatMap(restauranteEntity -> this.service.register(restauranteEntity));
    }


    @PutMapping("/save")
    public Mono<Boolean> updateRestaurante(@RequestBody Restaurante restaurante) {
        return this.mapper.map(Mono.just(restaurante))
                .flatMap(restauranteEntity -> this.service.update(restauranteEntity));
    }


    @DeleteMapping("/delete/{id}")
    public Mono<Boolean> deleteRestaurante(@PathVariable String id) {
        return this.service.deleteRestauranteById(id);
    }


    @GetMapping(value = "/get", params = "nombre")
    public Flux<Restaurante> getByNombre(@RequestParam String nombre) {
        return this.mapper.mapFlux(this.service.findByNombreContaining(nombre));
    }


    @GetMapping(value = "/get", params = "description")
    public Flux<Restaurante> getByDescription(@RequestParam String description) {
        return this.mapper.mapFlux(this.service.findByDescriptionContaining(description));
    }


    @GetMapping(value = "/get", params = "tag")
    public Flux<Restaurante> getByTag(@RequestParam String tag) {
        return this.mapper.mapFlux(this.service.findByTag(tag));
    }
}