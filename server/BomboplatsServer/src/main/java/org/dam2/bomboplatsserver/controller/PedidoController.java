package org.dam2.bomboplatsserver.controller;

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
    public Flux<Pedido> findAll() {
        return this.mapper.mapFlux(this.service.findAll());
    }

    @GetMapping("/get/{id}")
    public Mono<Pedido> getPedidoById(@PathVariable String id) {
        return this.mapper.unmap(this.service.findById(id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping("/register")
    public Mono<Boolean> register(@RequestBody Pedido pedido) {
        return this.mapper.map(Mono.just(pedido)).flatMap(pedidoEntity -> this.service.register(pedidoEntity));
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

    @PutMapping("/save")
    public Mono<Boolean> updatePedido(@RequestBody Pedido pedido) {
        return this.mapper.map(Mono.just(pedido)).flatMap(pedidoEntity -> this.service.update(pedidoEntity));
    }

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
