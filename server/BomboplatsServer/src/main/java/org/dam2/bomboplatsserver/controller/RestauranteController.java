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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Controlador REST para la gestión de restaurantes.
 * Expone endpoints para crear, consultar, actualizar y eliminar restaurantes,
 * así como para registrar platos y obtener URLs de subida de imágenes.
 */
@RestController
@RequestMapping("/restaurante")
public class RestauranteController {

    @Autowired private IRestauranteService service;
    @Autowired private IS3Service s3Service;
    @Autowired private IPlatoService platoService;
    @Autowired private IDireccionService direccionService;

    /**
     * Devuelve todos los restaurantes disponibles.
     *
     * @return flujo con todos los restaurantes
     */
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


    /**
     * Busca un restaurante por su id único, su clave principal.
     *
     * @param id identificador del restaurante
     * @return el restaurante encontrado, o 404 si no existe
     */
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

    /**
     * Registra un nuevo restaurante en el sistema.
     *
     * @param restaurante datos del restaurante a registrar
     * @return el restaurante creado con su ID asignado, o 400 si los datos son inválidos
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "Restaurante registrado con su ID generado")
    public Mono<Restaurante> register(@RequestBody Restaurante restaurante) {
        return this.service.register(restaurante)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)));
    }

    /**
     * Actualiza los datos de un restaurante existente.
     * Sincroniza también sus direcciones y platos asociados.
     *
     * @param restaurante datos actualizados del restaurante
     * @return {@code true} si se actualizó correctamente, {@code false} si no existía o hubo un error
     */
    @PutMapping("/save")
    @Operation(summary = "Actualizar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "true: Restaurante actualizado. false: No existía o hubo error")
    public Mono<Boolean> updateRestaurante(@RequestBody Restaurante restaurante) {
        Mono<Void> direccionesMono = syncDirecciones(restaurante);
        Mono<Void> platosMono = syncPlatos(restaurante);
        return Mono.when(direccionesMono, platosMono)
                .then(this.service.update(restaurante));
    }

    /**
     * Elimina un restaurante por su id.
     *
     * @param id identificador del restaurante a eliminar
     * @return {@code true} si se eliminó correctamente, {@code false} si no existía o hubo un error
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Eliminar un restaurante")
    @ApiResponse(responseCode = "200",
            description = "true: Restaurante eliminado. false: No existía o hubo error")
    public Mono<Boolean> deleteRestaurante(@PathVariable String id) {
        return this.service.deleteRestauranteById(id);
    }

    /**
     * Busca restaurantes cuyo nombre contenga el texto indicado.
     *
     * @param nombre texto a buscar en el nombre del restaurante
     * @return flujo de restaurantes que coinciden con la búsqueda
     */
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

    /**
     * Busca restaurantes cuya descripción contenga el texto indicado.
     *
     * @param description texto a buscar en la descripción del restaurante
     * @return flujo de restaurantes que coinciden con la búsqueda
     */
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

    /**
     * Busca restaurantes asociados a un tag concreto.
     *
     * @param tag etiqueta por la que filtrar
     * @return flujo de restaurantes que tienen ese tag
     */
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

    /**
     * Genera una URL prefirmada para subir la imagen de un restaurante a S3, AWS.
     *
     * @param id identificador del restaurante
     * @param index índice de la imagen (por defecto 0)
     * @return URL prefirmada como texto plano, o 404 si el restaurante no existe
     */
    @GetMapping(value = "/icon-upload-url/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
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
    /**
     * Registra un nuevo plato asociado a un restaurante existente.
     *
     * @param idRestaurante identificador del restaurante al que va a pertenecer el plato
     * @param plato datos del plato a registrar
     * @return el plato creado con su ID asignado, o 404 si el restaurante no existe
     */
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
    /**
     * Sincroniza las direcciones del restaurante asignándoles el ID correspondiente.
     * Se utiliza en actualizar restaurante.
     * No hace nada si la lista de direcciones está vacía o es nula.
     *
     * @param restaurante restaurante cuyas direcciones se van a sincronizar
     * @return Mono vacío al completarse la operación
     */
    private Mono<Void> syncDirecciones(Restaurante restaurante) {
        if (restaurante.getDirecciones() == null || restaurante.getDirecciones().isEmpty())
            return Mono.empty();

        return Flux.fromIterable(restaurante.getDirecciones())
                .flatMap(direccion -> this.direccionService.asignarRestauranteId(direccion, restaurante.getId()))
                .then();
    }
    /**
     * Sincroniza los platos del restaurante actualizándolos en el sistema.
     * Se utiliza en actualizar restaurante.
     * No hace nada si la lista de platos está vacía o es nula.
     *
     * @param restaurante restaurante cuyos platos se van a sincronizar
     * @return Mono vacío al completarse la operación
     */
    private Mono<Void> syncPlatos(Restaurante restaurante) {
        if (restaurante.getPlatos() == null || restaurante.getPlatos().isEmpty())
            return Mono.empty();

        return Flux.fromIterable(restaurante.getPlatos())
                .flatMap(plato -> this.platoService.update(plato))
                .then();
    }

}