package org.dam2.bomboplatsserver.controller;

import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.modelo.mapper.DireccionEntityMapper;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
        return this.mapper.unmap(this.service.findById(id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "user")
    public Flux<Direccion> getDireccionOfUser(@RequestParam(required = false) String user) {
        return this.mapper.mapFlux(this.service.getDireccionesOfUser(user))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "restaurante")
    public Flux<Direccion> getDireccionOfRestaurante(@RequestParam(required = false) String restaurante) {
        return this.mapper.mapFlux(this.service.getDireccionesOfRestaurante(restaurante))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/register")
    public Mono<Boolean> registerDireccion(@RequestBody Direccion direccion) {
        return this.mapper.map(Mono.just(direccion))
                .flatMap(direccionEntity -> this.service.register(direccionEntity));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Boolean> deleteById(@PathVariable String id) {
        return this.service.deleteDireccionByID(id);
    }

    @PutMapping("/save")
    public Mono<Boolean> updateDireccion(@RequestBody Direccion direccion) {
        return this.mapper.map(Mono.just(direccion)).flatMap(direccionEntity -> this.service.update(direccionEntity));
    }

    @PostMapping("/load")
    public Mono<String> load(@RequestBody DireccionEntity direccionEntity) {
        return this.service.register(direccionEntity).map(success -> {
            if (success) {
                return "Se ha registrado la direccion";
            } else {
                return "No se ha podido registrar la direccion";
            }
        });
    }
}
