package org.dam2.bomboplatsserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import org.dam2.bomboplatsserver.modelo.mapper.PedidoEntityMapper;
import org.dam2.bomboplatsserver.service.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired private IPedidoService service;
    @Autowired private PedidoEntityMapper mapper;

    @GetMapping("/get")
    @Operation(summary = "Obtener todos los pedidos o según su id/estado/user/plato")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Pedidos encontrados",
                    content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "404", description = "No se han encontrado pedidos")
    })
    public Flux<Pedido> findAll() {
        return this.mapper.mapFlux(this.service.findAll())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "id")
    public Mono<Pedido> getPedidoById(@RequestParam(required = false) String id) {
        return this.mapper.unmap(this.service.findById(id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "estado")
    public Flux<Pedido> getPedidosByEstado(@RequestParam(required = false) String estado) {
        String estadoUpper = estado.toUpperCase();
        Pedido.Estado est;
        try {
            est = Pedido.Estado.valueOf(estadoUpper);
        } catch (IllegalArgumentException e) {
            return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND)); // Si por alguna razon le pasan un estado que no existe, pues que devuelva un Flux vacio en vez de que se la pegue
        }
        return this.mapper.mapFlux(this.service.findByEstado(est))
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "user")
    public Flux<Pedido> getPedidosByUser(@RequestParam(required = false) String user) {
        return this.mapper.mapFlux(this.service.findByUserId(user))
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping(value = "/get", params = "plato")
    public Flux<Pedido> getPedidosByPlato(@RequestParam(required = false) String plato) {
        return this.mapper.mapFlux(this.service.findByPlatoId(plato))
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un pedido")
    @ApiResponse(responseCode = "200", description = "true: Pedido registrado. false: Ese registro ya existe o se ha producido un error")
    public Mono<Boolean> register(@RequestBody Pedido pedido) {
        return this.mapper.map(Mono.just(pedido)).flatMap(pedidoEntity -> this.service.register(pedidoEntity));
    }

    @PutMapping("/save")
    @Operation(summary = "Actualizar un pedido")
    @ApiResponse(responseCode = "200", description = "true: Pedido actualizado. false: Ese registro no existe o se ha producido un error")
    public Mono<Boolean> updatePedido(@RequestBody Pedido pedido) {
        return this.mapper.map(Mono.just(pedido)).flatMap(pedidoEntity -> this.service.update(pedidoEntity));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Borrar un pedido")
    @ApiResponse(responseCode = "200", description = "true: Pedido borrado. false: Ese registro no existe o se ha producido un error")
    public Mono<Boolean> deletePedido(@PathVariable String id) {
        return this.service.deletePedidoById(id);
    }

    // SOLO PARA TESTEO
    @PostMapping("/load")
    public Mono<String> load(@RequestBody PedidoEntity pedidoEntity) {
        return this.service.register(pedidoEntity).map(success -> {
            if (success) {
                return "Se ha registrado el pedido";
            } else {
                return "No se ha podido registrar el pedido";
            }
        });
    }

}
