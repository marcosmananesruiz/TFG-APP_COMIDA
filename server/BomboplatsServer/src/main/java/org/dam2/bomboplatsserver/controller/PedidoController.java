package org.dam2.bomboplatsserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.service.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired private IPedidoService service;

    @GetMapping("/getAll")
    @Operation(summary = "Obtener todos los pedidos")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Pedidos encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Pedido.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado pedidos")
    })
    public Flux<Pedido> findAll() {
        return this.service.findAll();
    }

    @GetMapping(value = "/get", params = "id")
    @Operation(summary = "Obtener un pedido segun su ID", operationId = "getPedidoById")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Pedidos Encontrados",
                    content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado el pedido")
    })
    public Mono<Pedido> getPedidoById(@RequestParam(required = false) String id) {
        return this.service.findById(id);
    }

    @GetMapping(value = "/getByEstado", params = "estado")
    @Operation(summary = "Obtener pedidos segun su estado", operationId = "getPedidoByEstado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos Encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Pedido.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado pedidos"),
            @ApiResponse(responseCode = "500", description = "Parametros incorrectos")
    })

    public Flux<Pedido> getPedidosByEstado(@RequestParam(required = false) Pedido.Estado estado) {
        return this.service.findByEstado(estado);
    }

    @GetMapping(value = "/getByUser", params = "user")
    @Operation(summary = "Obtener todos los pedidos de un usuario según su id", operationId = "getPedidoByUser")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos Encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Pedido.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado pedidos"),
            @ApiResponse(responseCode = "500", description = "Parametros incorrects")
    })
    public Flux<Pedido> getPedidosByUser(@RequestParam(required = false) String user) {
        return this.service.findByUserId(user);
    }

    @GetMapping(value = "/getByPlato", params = "plato")
    @Operation(summary = "Obtener todos los pedidos que se han hecho de un plato según su id", operationId = "getPedidoByPlato")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos Encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Pedido.class)))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado pedidos"),
            @ApiResponse(responseCode = "500", description = "Parametros incorrectos")
    })
    public Flux<Pedido> getPedidosByPlato(@RequestParam(required = false) String plato) {
        return this.service.findByPlatoId(plato);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un pedido")
    @ApiResponse(responseCode = "200", description = "true: Pedido registrado. false: Ese registro ya existe o se ha producido un error")
    public Mono<Pedido> register(@RequestBody Pedido pedido) {
        return this.service.register(pedido);
    }

    @PutMapping("/save")
    @Operation(summary = "Actualizar un pedido")
    @ApiResponse(responseCode = "200", description = "true: Pedido actualizado. false: Ese registro no existe o se ha producido un error")
    public Mono<Boolean> updatePedido(@RequestBody Pedido pedido) {
        return this.service.update(pedido);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Borrar un pedido")
    @ApiResponse(responseCode = "200", description = "true: Pedido borrado. false: Ese registro no existe o se ha producido un error")
    public Mono<Boolean> deletePedido(@PathVariable String id) {
        return this.service.deletePedidoById(id);
    }

    @GetMapping("/get/id")
    @Operation(summary = "Obtener unicamente el id de los pedidos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Se han obtenido todos los IDs exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "404", description = "No hay ningun ID")
    })
    public Mono<List<String>> getPedidosIDs() {
        return this.service.getIDs().collectList();
    }
}
