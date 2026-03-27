package org.dam2.bomboplatsserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplats.api.Restaurante;
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
    @Autowired private IS3Service s3Service;
    @Autowired private IPlatoService platoService;
    @Autowired private IDireccionService direccionService;

    @GetMapping("/getAll")
    @Operation(summary = "Obtener todos los restaurantes")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurantes encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado restaurantes")
    })
    public Flux<Restaurante> findAll() {
        return this.service.findAll();
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
        return this.service.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "Restaurante registrado con su ID generado")
    public Mono<Restaurante> register(@RequestBody Restaurante restaurante) {
        return this.service.register(restaurante)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)));
    }

    @PutMapping("/save")
    @Operation(summary = "Actualizar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "true: Restaurante actualizado. false: No existía o hubo error")
    public Mono<Boolean> updateRestaurante(@RequestBody Restaurante restaurante) {
        Mono<Void> direccionesMono = syncDirecciones(restaurante);
        return direccionesMono.then(this.service.update(restaurante));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "true: Restaurante eliminado. false: No existía o hubo error")
    public Mono<Boolean> deleteRestaurante(@PathVariable String id) {
        return this.service.deleteRestauranteById(id);
    }

    @GetMapping(value = "/getpornombre", params = "nombre")
    @Operation(summary = "Buscar restaurantes por nombre")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurantes encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado restaurantes")
    })
    public Flux<Restaurante> getByNombre(@RequestParam String nombre) {
        return this.service.findByNombreContaining(nombre);
    }

    @GetMapping(value = "/getporescription", params = "description")
    @Operation(summary = "Buscar restaurantes por descripción")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurantes encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado restaurantes")
    })
    public Flux<Restaurante> getByDescription(@RequestParam String description) {
        return this.service.findByDescriptionContaining(description);
    }

    @GetMapping(value = "/getportag", params = "tag")
    @Operation(summary = "Buscar restaurantes por tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Restaurantes encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Restaurante.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado restaurantes")
    })
    public Flux<Restaurante> getByTag(@RequestParam String tag) {
        return this.service.findByTag(tag);
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

    @PostMapping("/{idRestaurante}/plato/register")
    @Operation(summary = "Registrar un plato asociado a un restaurante")
    @ApiResponse(responseCode = "200",
            description = "Plato registrado con su ID generado")
    public Mono<Plato> registerPlato(
            @PathVariable String idRestaurante,
            @RequestBody Plato plato) {
        return this.service.findById(idRestaurante)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(r -> this.platoService.registerConRestaurante(plato, idRestaurante));
    }

    private Mono<Void> syncDirecciones(Restaurante restaurante) {
        if (restaurante.getDirecciones() == null || restaurante.getDirecciones().isEmpty())
            return Mono.empty();

        return Flux.fromIterable(restaurante.getDirecciones())
                .flatMap(direccion -> this.direccionService.asignarRestauranteId(direccion, restaurante.getId()))
                .then();
    }
}