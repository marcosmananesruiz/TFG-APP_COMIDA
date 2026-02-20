package org.dam2.bomboplatsserver.controller;

import org.dam2.bomboplats.api.Pedido;
import org.dam2.bomboplatsserver.modelo.entity.PedidoEntity;
import org.dam2.bomboplatsserver.modelo.mapper.PedidoEntityMapper;
import org.dam2.bomboplatsserver.service.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public Mono<Pedido> findById(@PathVariable String id) {
        return this.mapper.unmap(this.service.findById(id));
    }

    @PostMapping("/register")
    public Mono<Boolean> register(@RequestBody Pedido pedido) {
        return this.mapper.map(Mono.just(pedido)).flatMap(pedidoEntity -> this.service.register(pedidoEntity));
    }
}
