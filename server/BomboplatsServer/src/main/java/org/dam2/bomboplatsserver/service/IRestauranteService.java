package org.dam2.bomboplatsserver.service;

import org.dam2.bomboplats.api.Restaurante;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Interfaz del servicio de restaurantes.
 * Define las operaciones disponibles para gestionar restaurantes,
 * incluyendo CRUD y búsquedas por distintos criterios.
 */
public interface IRestauranteService {

    /**
     * Busca un restaurante por su identificador.
     *
     * @param id identificador del restaurante
     * @return Mono con el restaurante completo, o vacío si no existe
     */
    Mono<Restaurante> findById(String id);

    /**
     * Devuelve todos los restaurantes.
     *
     * @return Flux con todos los restaurantes
     */
    Flux<Restaurante> findAll();

    /**
     * Registra un nuevo restaurante.
     *
     * @param restaurante datos del restaurante a registrar
     * @return Mono con el restaurante creado y su ID asignado
     */
    Mono<Restaurante> register(Restaurante restaurante);

    /**
     * Actualiza los datos de un restaurante existente.
     *
     * @param restaurante datos actualizados del restaurante
     * @return {@code true} si se actualizó correctamente, {@code false} si no existía
     */
    Mono<Boolean> update(Restaurante restaurante);

    /**
     * Elimina un restaurante y todos sus platos y direcciones asociados.
     *
     * @param id identificador del restaurante a eliminar
     * @return {@code true} si se eliminó correctamente, {@code false} si no existía
     */
    Mono<Boolean> deleteRestauranteById(String id);


    /**
     * Busca restaurantes cuyo nombre coincida exactamente con el indicado.
     *
     * @param nombre nombre exacto a buscar
     * @return Flux con los restaurantes que coinciden
     */
    Flux<Restaurante> findByNombre(String nombre);

    /**
     * Busca restaurantes cuyo nombre contenga el texto indicado.
     *
     * @param nombre texto a buscar en el nombre del restaurante
     * @return Flux con los restaurantes que coinciden
     */
    Flux<Restaurante> findByNombreContaining(String nombre);

    /**
     * Busca restaurantes cuya descripción contenga el texto indicado.
     *
     * @param description texto a buscar en la descripción
     * @return Flux con los restaurantes que coinciden
     */
    Flux<Restaurante> findByDescriptionContaining(String description);

    /**
     * Busca restaurantes que tengan asignado un tag concreto.
     *
     * @param tag etiqueta por la que filtrar
     * @return Flux con los restaurantes que tienen ese tag
     */
    Flux<Restaurante> findByTag(String tag);
}