package org.dam2.bomboplatsserver.modelo.mapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz genérica para la conversión entre entidades de base de datos y modelos de API.
 *
 * @param <A> tipo de la entidad de base de datos
 * @param <B> tipo del modelo de API (DTO)
 */
public interface EntityMapper<A,B> {
    /**
     * Convierte un DTO al tipo de entidad de base de datos.
     *
     * @param o Mono con el DTO a convertir
     * @return Mono con la entidad resultante
     */
    Mono<A> map(Mono<B> o);
    /**
     * Convierte una entidad de base de datos al tipo de DTO.
     *
     * @param o Mono con la entidad a convertir
     * @return Mono con el DTO resultante
     */
    Mono<B> unmap(Mono<A> o);
    /**
     * Convierte un flujo de entidades de base de datos a un flujo de DTOs.
     *
     * @param o Flux con las entidades a convertir
     * @return Flux con los DTOs resultantes
     */
    Flux<B> mapFlux(Flux<A> o);
}
