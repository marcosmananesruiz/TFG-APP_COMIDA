package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplats.api.Plato;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz del servicio de platos.
 * Define las operaciones disponibles para gestionar platos,
 * incluyendo CRUD y búsquedas por distintos criterios.
 */
public interface IPlatoService {

    /**
     * Busca un plato por su identificador.
     *
     * @param id identificador del plato
     * @return Mono con el plato, o vacío si no existe
     */
    Mono<Plato> findById(String id);

    /**
     * Devuelve todos los platos del sistema.
     *
     * @return Flux con todos los platos
     */
    Flux<Plato> findAll();

    /**
     * Registra un nuevo plato en el sistema.
     *
     * @param plato datos del plato a registrar
     * @return Mono con el plato creado y su ID asignado
     */
    Mono<Plato> register(Plato plato);


    /**
     * Actualiza los datos de un plato existente.
     *
     * @param plato datos actualizados del plato
     * @return {@code true} si se actualizó correctamente, {@code false} si no existía
     */
    Mono<Boolean> update(Plato plato);

    /**
     * Elimina un plato por su identificador.
     * Si tiene pedidos asociados, los elimina previamente.
     *
     * @param id identificador del plato a eliminar
     * @return {@code true} si se eliminó correctamente, {@code false} si no existía
     */
    Mono<Boolean> deletePlatoById(String id);

    /**
     * Devuelve todos los platos pertenecientes a un restaurante concreto.
     * por su identificador.
     *
     * @param idRestaurante identificador del restaurante
     * @return Flux con los platos de ese restaurante
     */
    Flux<Plato> findByIdRestaurante(String idRestaurante);

    /**
     * Busca platos cuyo nombre contenga el texto indicado.
     *
     * @param nombre texto a buscar en el nombre del plato
     * @return Flux con los platos que coinciden
     */
    Flux<Plato> findByNombreContaining(String nombre);

    /**
     * Busca platos de un restaurante cuyo nombre contenga el texto indicado.
     *
     * @param idRestaurante identificador del restaurante
     * @param nombre        texto a buscar en el nombre del plato
     * @return Flux con los platos que coinciden con ambos criterios
     */
    Flux<Plato> findByIdRestauranteAndNombreContaining(String idRestaurante, String nombre);

    /**
     * Busca platos que tengan asignado un tag concreto.
     *
     * @param tag etiqueta por la que filtrar
     * @return Flux con los platos que tienen ese tag
     */
    Flux<Plato> findByTag(String tag);

    /**
     * Registra un nuevo plato asociándolo directamente a un restaurante.
     *
     * @param plato         datos del plato a registrar
     * @param idRestaurante identificador del restaurante al que pertenece
     * @return Mono con el plato creado y su ID asignado
     */
    Mono<Plato> registerConRestaurante(Plato plato, String idRestaurante);
}