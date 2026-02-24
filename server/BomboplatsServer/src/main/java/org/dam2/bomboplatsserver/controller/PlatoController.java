package org.dam2.bomboplatsserver.controller;

import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.dam2.bomboplatsserver.modelo.mapper.PlatoEntityMapper;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/plato")
public class PlatoController {

    @Autowired private IPlatoService service;
    @Autowired private PlatoEntityMapper mapper;


    @GetMapping("/get")
    public Flux<Plato> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }

    @GetMapping("/get/{id}")
    public Mono<Plato> getPlatoById(@PathVariable String id) {
        return this.mapper.unmap(this.service.findById(id));
    }


    @PostMapping("/register")
    public Mono<Boolean> register(@RequestBody Plato plato) {
        return this.mapper.map(Mono.just(plato))
                .flatMap(platoEntity -> this.service.register(platoEntity));
    }


    @PutMapping("/save")
    public Mono<Boolean> updatePlato(@RequestBody Plato plato) {
        return this.mapper.map(Mono.just(plato))
                .flatMap(platoEntity -> this.service.update(platoEntity));
    }


    @DeleteMapping("/delete/{id}")
    public Mono<Boolean> deletePlato(@PathVariable String id) {
        return this.service.deletePlatoById(id);
    }



    @GetMapping(value = "/get", params = "nombre")
    public Flux<Plato> getByNombre(@RequestParam String nombre) {
        return this.mapper.mapFlux(this.service.findByNombreContaining(nombre));
    }

    @GetMapping(value = "/get", params = "idRestaurante")
    public Flux<Plato> getByRestaurante(@RequestParam String idRestaurante) {
        return this.mapper.mapFlux(this.service.findByIdRestaurante(idRestaurante));
    }

    @GetMapping(value = "/get", params = {"idRestaurante", "nombre"})
    public Flux<Plato> getByRestauranteAndNombre(@RequestParam String idRestaurante,
                                                 @RequestParam String nombre) {
        return this.mapper.mapFlux(this.service.findByIdRestauranteAndNombreContaining(idRestaurante, nombre));
    }

    @GetMapping(value = "/get", params = "tag")
    public Flux<Plato> getByTag(@RequestParam String tag) {
        return this.mapper.mapFlux(this.service.findByTag(tag));
    }
}