package org.dam2.bomboplatsserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplatsserver.modelo.mapper.PlatoEntityMapper;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.dam2.bomboplatsserver.service.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/plato")
public class PlatoController {

    @Autowired private IPlatoService service;
    @Autowired private PlatoEntityMapper mapper;
    @Autowired private IS3Service s3Service;

    @GetMapping("/getAll")
    @Operation(summary = "Obtener todos los platos")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Platos encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Plato.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado platos")
    })
    public Flux<Plato> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Obtener un plato por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Plato encontrado",
                    content = @Content(schema = @Schema(implementation = Plato.class))),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado el plato"),
            @ApiResponse(responseCode = "500", description = "Parámetros incorrectos")
    })
    public Mono<Plato> getPlatoById(@PathVariable String id) {
        return this.mapper.unmap(this.service.findById(id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo plato")
    @ApiResponse(responseCode = "200",
            description = "true: Plato registrado. false: Ya existía o hubo un error")
    public Mono<Boolean> register(@RequestBody Plato plato) {
        return this.mapper.map(Mono.just(plato))
                .flatMap(platoEntity -> this.service.register(platoEntity));
    }

    @PutMapping("/save")
    @Operation(summary = "Actualizar un plato existente")
    @ApiResponse(responseCode = "200",
            description = "true: Plato actualizado. false: No existía o hubo error")
    public Mono<Boolean> updatePlato(@RequestBody Plato plato) {
        return this.mapper.map(Mono.just(plato))
                .flatMap(platoEntity -> this.service.update(platoEntity));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un plato por su ID")
    @ApiResponse(responseCode = "200",
            description = "true: Plato eliminado. false: No existía o hubo error")
    public Mono<Boolean> deletePlato(@PathVariable String id) {
        return this.service.deletePlatoById(id);
    }

    @GetMapping(value = "/get", params = "nombre")
    @Operation(summary = "Buscar platos por nombre")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Platos encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Plato.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado platos")
    })
    public Flux<Plato> getByNombre(@RequestParam String nombre) {
        return this.mapper.mapFlux(this.service.findByNombreContaining(nombre));
    }

    @GetMapping(value = "/get", params = "idRestaurante")
    @Operation(summary = "Buscar platos por ID de restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Platos encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Plato.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado platos")
    })
    public Flux<Plato> getByRestaurante(@RequestParam String idRestaurante) {
        return this.mapper.mapFlux(this.service.findByIdRestaurante(idRestaurante));
    }

    @GetMapping(value = "/get", params = {"idRestaurante", "nombre"})
    @Operation(summary = "Buscar platos por restaurante y nombre")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Platos encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Plato.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado platos")
    })
    public Flux<Plato> getByRestauranteAndNombre(
            @RequestParam String idRestaurante,
            @RequestParam String nombre) {
        return this.mapper.mapFlux(
                this.service.findByIdRestauranteAndNombreContaining(idRestaurante, nombre));
    }

    @GetMapping(value = "/get", params = "tag")
    @Operation(summary = "Buscar platos por tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Platos encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Plato.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado platos")
    })
    public Flux<Plato> getByTag(@RequestParam String tag) {
        return this.mapper.mapFlux(this.service.findByTag(tag));
    }

    @GetMapping("/icon-upload-url/{id}")
    @Operation(summary = "Obtener URL prefirmada para subir foto de plato")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL generada"),
            @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    public Mono<String> getPlatoIconUploadUrl(@PathVariable String id) {
        return this.service.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(p -> this.s3Service.generatePlatoIconUrl(id));
    }
}