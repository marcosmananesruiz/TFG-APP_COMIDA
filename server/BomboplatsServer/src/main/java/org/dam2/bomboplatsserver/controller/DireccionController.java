package org.dam2.bomboplatsserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Obtener direcciones")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Direcciones encontradas",
                    content = @Content(schema = @Schema(implementation = Direccion.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron direcciones")
    })
    public Flux<Direccion> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }

    @GetMapping(value = "/get", params = "id")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Dirección encontrada",
                    content = @Content(schema = @Schema(implementation = Direccion.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la dirección")
    })
    public Mono<Direccion> getDireccionById(@RequestParam(required = false) String id) {
        return this.mapper.unmap(this.service.findById(id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "user")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Dirección encontrada",
                    content = @Content(schema = @Schema(implementation = Direccion.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la dirección")
    })
    public Flux<Direccion> getDireccionOfUser(@RequestParam(required = false) String user) {
        return this.mapper.mapFlux(this.service.getDireccionesOfUser(user))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Dirección encontrada",
                    content = @Content(schema = @Schema(implementation = Direccion.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la dirección")
    })
    public Flux<Direccion> getDireccionOfRestaurante(@RequestParam(required = false) String restaurante) {
        return this.mapper.mapFlux(this.service.getDireccionesOfRestaurante(restaurante))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar una dirección")
    @ApiResponse(responseCode = "200", description = "true: Registro exitoso. false: El registro ya existe o se produjo un error")
    public Mono<Boolean> registerDireccion(@RequestBody Direccion direccion) {
        return this.mapper.map(Mono.just(direccion))
                .flatMap(direccionEntity -> this.service.register(direccionEntity));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Borrar una dirección por su Id")
    @ApiResponse(responseCode = "200", description = "true: Borrado exitoso. false: El registro no existe o se produjo un error")
    public Mono<Boolean> deleteById(@PathVariable String id) {
        return this.service.deleteDireccionByID(id);
    }

    @PutMapping("/save")
    @Operation(summary = "Actualizar una dirección")
    @ApiResponse(responseCode = "200", description = "true: Actualization exitosa. false: El registro no existe o se produjo un error")
    public Mono<Boolean> updateDireccion(@RequestBody Direccion direccion) {
        return this.mapper.map(Mono.just(direccion)).flatMap(direccionEntity -> this.service.update(direccionEntity));
    }

    // SOLO PARA TESTEO
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
