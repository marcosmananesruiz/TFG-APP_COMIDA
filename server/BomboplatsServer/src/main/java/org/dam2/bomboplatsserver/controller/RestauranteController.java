package org.dam2.bomboplatsserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.Restaurante;
import org.dam2.bomboplatsserver.modelo.mapper.DireccionEntityMapper;
import org.dam2.bomboplatsserver.modelo.mapper.PlatoEntityMapper;
import org.dam2.bomboplatsserver.modelo.mapper.RestauranteEntityMapper;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.dam2.bomboplatsserver.service.IRestauranteService;
import org.dam2.bomboplatsserver.service.IS3Service;
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
    @Autowired private IS3Service s3Service;
    @Autowired private IPlatoService platoService;
    @Autowired private PlatoEntityMapper platoMapper;
    @Autowired private IDireccionService direccionService;
    @Autowired private DireccionEntityMapper direccionMapper;

    @GetMapping("/getAll")
    @Operation(summary = "Obtener todos los restaurantes")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurantes encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
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
        Mono<Void> platosMono = syncPlatos(restaurante);
        Mono<Void> direccionesMono = syncDirecciones(restaurante);

        return this.mapper.map(Mono.just(restaurante))
                .flatMap(restauranteEntity -> this.service.register(restauranteEntity))
                .flatMap(success -> {
                    if (!success) return Mono.just(false);
                    return Mono.when(platosMono, direccionesMono).thenReturn(true);
                });
    }

    @PutMapping("/save")
    @Operation(summary = "Actualizar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "true: Restaurante actualizado. false: No existía o hubo error")
    public Mono<Boolean> updateRestaurante(@RequestBody Restaurante restaurante) {
        Mono<Void> platosMono = syncPlatos(restaurante);
        Mono<Void> direccionesMono = syncDirecciones(restaurante);

        return this.mapper.map(Mono.just(restaurante))
                .flatMap(restauranteEntity -> Mono.when(platosMono, direccionesMono)
                        .then(this.service.update(restauranteEntity)));
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
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
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
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
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
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado restaurantes")
    })
    public Flux<Restaurante> getByTag(@RequestParam String tag) {
        return this.mapper.mapFlux(this.service.findByTag(tag));
    }

    @GetMapping("/icon-upload-url/{id}")
    @Operation(summary = "Obtener URL prefirmada para subir foto de restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL generada"),
            @ApiResponse(responseCode = "404", description = "Restaurante no encontrado")
    })
    public Mono<String> getRestauranteIconUploadUrl(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int index) {
        return this.service.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(r -> this.s3Service.generateRestauranteIconUrl(id, index));
    }

    // ---- Métodos privados de sincronización ----

    private Mono<Void> syncPlatos(Restaurante restaurante) {
        if (restaurante.getPlatos() == null || restaurante.getPlatos().isEmpty())
            return Mono.empty();

        return Flux.fromIterable(restaurante.getPlatos())
                .flatMap(plato -> this.platoMapper.map(Mono.just(plato))
                        .flatMap(platoEntity -> {
                            platoEntity.setIdRestaurante(restaurante.getId());
                            return this.platoService.update(platoEntity)
                                    .flatMap(updated -> {
                                        if (!updated) return this.platoService.register(platoEntity);
                                        return Mono.just(true);
                                    });
                        })
                ).then();
    }

    private Mono<Void> syncDirecciones(Restaurante restaurante) {
        if (restaurante.getDirecciones() == null || restaurante.getDirecciones().isEmpty())
            return Mono.empty();

        return Flux.fromIterable(restaurante.getDirecciones())
                .flatMap(direccion -> this.direccionMapper.map(Mono.just(direccion))
                        .flatMap(direccionEntity -> {
                            direccionEntity.setIdRestaurante(restaurante.getId());
                            return this.direccionService.update(direccionEntity)
                                    .flatMap(updated -> {
                                        if (!updated) return this.direccionService.register(direccionEntity);
                                        return Mono.just(true);
                                    });
                        })
                ).then();
    }
}