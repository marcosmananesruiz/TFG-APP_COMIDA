package org.dam2.bomboplatsserver.modelo.mapper;

import reactor.core.publisher.Mono;

public interface EntityMapper<A,B> {

    Mono<A> map(Mono<B> o);
    Mono<B> unmap(Mono<A> o);
}
