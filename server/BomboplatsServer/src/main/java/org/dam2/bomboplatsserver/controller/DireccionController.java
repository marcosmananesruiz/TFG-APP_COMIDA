package org.dam2.bomboplatsserver.controller;

import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplatsserver.modelo.mapper.DireccionEntityMapper;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/direccion")
public class DireccionController {

    @Autowired private IDireccionService service;
    @Autowired private DireccionEntityMapper mapper;

    @GetMapping("/get")
    public Flux<Direccion> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }

    @GetMapping("/get/{id}")
    public Mono<Direccion> getDireccionById(@PathVariable String id) {
        return this.mapper.unmap(this.service.findById(id));
    }

    @PostMapping("/register")
    public Mono<Boolean> registerDireccion(@RequestBody Direccion direccion) {
        return this.mapper.map(Mono.just(direccion))
                .flatMap(direccionEntity -> this.service.register(direccionEntity));
    }

    @PutMapping("/delete/{id}")
    public Mono<Boolean> deleteById(@PathVariable String id) {
        return this.service.deleteDireccionByID(id);
    }
}
