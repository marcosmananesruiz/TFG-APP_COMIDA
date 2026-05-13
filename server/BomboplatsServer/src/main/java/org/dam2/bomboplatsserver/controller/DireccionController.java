package org.dam2.bomboplatsserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Controlador REST para el manejo de las direcciones
 */
@RestController
@RequestMapping("/direccion")
public class DireccionController {

    @Autowired private IDireccionService service;

    /**
     * Obtener todas las direcciones
     * @return {@link Flux}<{@link Direccion}> con todas las direcciones
     */
    @GetMapping("/getAll")
    @Operation(summary = "Obtener direcciones")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Direcciones encontradas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Direccion.class)))),
            @ApiResponse(responseCode = "404", description = "No se encontraron direcciones")
    })
    public Flux<Direccion> findAll() {
        return this.service.findAll();
    }

    /**
     * Obtener una direccion con su ID
     * @param id Id de la direccion a obtener
     * @return {@link Mono}<{@link Direccion}> con la direccion correspondiente
     */
    @GetMapping(value = "/get", params = "id")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Dirección encontrada",
                    content = @Content(schema = @Schema(implementation = Direccion.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró la dirección"),
            @ApiResponse(responseCode = "500", description = "parametros incorrectos")
    })
    public Mono<Direccion> getDireccionById(@RequestParam(required = false) String id) {
        return this.service.findById(id);
    }

    /**
     * Obtener las direcciones de un usuario
     * @param user Id del usuario del que se quiere obtener las direcciones
     * @return {@link Flux}<{@link Direccion}> con las direcciones asignadas al usuario
     */
    @GetMapping(value = "/getByUser", params = "user")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Dirección encontrada",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Direccion.class)))),
            @ApiResponse(responseCode = "404", description = "No se encontró la dirección"),
            @ApiResponse(responseCode = "500", description = "Parametros incorrectos")
    })
    public Flux<Direccion> getDireccionOfUser(@RequestParam(required = false) String user) {
        return this.service.getDireccionesOfUser(user);
    }

    /**
     * Obtener todas las direcciones de un restaurante
     * @param restaurante Id del restaurante del que se quiere obtener las direcciones
     * @return {@link Flux}<{@link Direccion}> con las direcciones asignadas al restaurante
     */
    @GetMapping(value = "/getByRestaurante", params = "restaurante")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Dirección encontrada",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Direccion.class)))),
            @ApiResponse(responseCode = "404", description = "No se encontró la dirección"),
            @ApiResponse(responseCode = "500", description = "Parametros incorrectos")
    })
    public Flux<Direccion> getDireccionOfRestaurante(@RequestParam(required = false) String restaurante) {
        return this.service.getDireccionesOfRestaurante(restaurante);
    }

    /**
     * Registrar una direccion
     * @param direccion Direccion a registrar
     * @return {@link Mono}<{@link Direccion}> con la direccion registrada (Campo Id actualizado)
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar una dirección")
    @ApiResponse(responseCode = "200", description = "true: Registro exitoso. false: El registro ya existe o se produjo un error")
    public Mono<Direccion> registerDireccion(@RequestBody Direccion direccion) {
        return this.service.register(direccion);
    }

    /**
     * Borrar una direccion
     * @param id Id de la Dirección a borrar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se ha borrado correctamente, {@code false} si no
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Borrar una dirección por su Id")
    @ApiResponse(responseCode = "200", description = "true: Borrado exitoso. false: El registro no existe o se produjo un error")
    public Mono<Boolean> deleteById(@PathVariable String id) {
        return this.service.deleteDireccionByID(id);
    }

    /**
     * Actualizar una direccion
     * @param direccion Dirección a actualizar
     * @return {@link Mono}<{@link Boolean}> con {@code true} si se ha actualizado correctamente, {@code false} si no
     */
    @PutMapping("/save")
    @Operation(summary = "Actualizar una dirección")
    @ApiResponse(responseCode = "200", description = "true: Actualization exitosa. false: El registro no existe o se produjo un error")
    public Mono<Boolean> updateDireccion(@RequestBody Direccion direccion) {
        return this.service.update(direccion);
    }

    /**
     * Obtener todas las IDs de las direcciones
     * @return {@link Mono}<{@link List}<{@link String}>> con todos los IDs
     */
    @GetMapping("/get/id")
    @Operation(summary = "Obtener unicamente los ID de las direcciones")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Se han obtenido todos los IDs exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado IDs")
    })
    public Mono<List<String>> getDireccionIDs() {
        return this.service.getIDs().collectList();
    }
}
