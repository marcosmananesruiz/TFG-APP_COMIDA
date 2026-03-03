package org.dam2.bomboplatsserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.dam2.bomboplats.api.Restaurante;
import org.dam2.bomboplatsserver.modelo.mapper.RestauranteEntityMapper;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.dam2.bomboplatsserver.service.IRestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/restaurante")
public class RestauranteController {

    @Autowired private IRestauranteService service;
    @Autowired private RestauranteEntityMapper mapper;

    @GetMapping("/get")
    @Operation(summary = "Obtener todos los restaurantes o filtrar por parámetros")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurantes encontrados",
                    content = @Content(schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado restaurantes")
    })
    public Flux<Restaurante> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Obtener restaurante por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurante encontrado",
                    content = @Content(schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado el restaurante"),
            @ApiResponse(responseCode = "500", description = "Parámetros incorrectos")
    })
    public Mono<Restaurante> getRestauranteById(@PathVariable String id) {
        return this.mapper.unmap(this.service.findById(id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "true: Restaurante registrado. false: Ya existía o hubo error")
    public Mono<Boolean> register(@RequestBody Restaurante restaurante) {
        return this.mapper.map(Mono.just(restaurante))
                .flatMap(restauranteEntity -> this.service.register(restauranteEntity));
    }

    @PutMapping("/save")
    @Operation(summary = "Actualizar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "true: Restaurante actualizado. false: No existía o hubo error")
    public Mono<Boolean> updateRestaurante(@RequestBody Restaurante restaurante) {
        return this.mapper.map(Mono.just(restaurante))
                .flatMap(restauranteEntity -> this.service.update(restauranteEntity));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "true: Restaurante eliminado. false: No existía o hubo error")
    public Mono<Boolean> deleteRestaurante(@PathVariable String id) {
        return this.service.deleteRestauranteById(id);
    }

    @GetMapping(value = "/get", params = "nombre")
    @Operation(summary = "Buscar restaurantes por nombre")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurantes encontrados",
                    content = @Content(schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado restaurantes")
    })
    public Flux<Restaurante> getByNombre(@RequestParam String nombre) {
        return this.mapper.mapFlux(this.service.findByNombreContaining(nombre));
    }

    @GetMapping(value = "/get", params = "description")
    @Operation(summary = "Buscar restaurantes por descripción")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurantes encontrados",
                    content = @Content(schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado restaurantes")
    })
    public Flux<Restaurante> getByDescription(@RequestParam String description) {
        return this.mapper.mapFlux(this.service.findByDescriptionContaining(description));
    }

    @GetMapping(value = "/get", params = "tag")
    @Operation(summary = "Buscar restaurantes por tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurantes encontrados",
                    content = @Content(schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado restaurantes")
    })
    public Flux<Restaurante> getByTag(@RequestParam String tag) {
        return this.mapper.mapFlux(this.service.findByTag(tag));
    }
}