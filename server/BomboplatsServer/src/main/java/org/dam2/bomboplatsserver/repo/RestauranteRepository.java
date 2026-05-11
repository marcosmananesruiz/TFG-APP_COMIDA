package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para la entidad {@link RestauranteEntity}.
 * Extiende {@link ReactiveCrudRepository} con consultas adicionales
 * para búsquedas por nombre, descripción y tags.
 */
public interface RestauranteRepository extends ReactiveCrudRepository<RestauranteEntity, String> {


    /**
     * Busca restaurantes cuyo nombre coincida exactamente con el indicado.
     *
     * @param nombre nombre exacto a buscar
     * @return Flux con los restaurantes que coinciden
     */
    Flux<RestauranteEntity> findByNombre(String nombre);

    /**
     * Busca restaurantes cuyo nombre coincida exactamente con el indicado,
     * sin distinguir mayúsculas.
     *
     * @param nombre nombre a buscar
     * @return Flux con los restaurantes que coinciden
     */
    Flux<RestauranteEntity> findByNombreIgnoreCase(String nombre);

    /**
     * Busca restaurantes cuyo nombre contenga el texto indicado,
     * sin distinguir mayúsculas.
     *
     * @param nombre texto a buscar en el nombre
     * @return Flux con los restaurantes que coinciden
     */
    Flux<RestauranteEntity> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca restaurantes cuya descripción contenga el texto indicado,
     * sin distinguir mayúsculas.
     *
     * @param description texto a buscar en la descripción
     * @return Flux con los restaurantes que coinciden
     */
    Flux<RestauranteEntity> findByDescriptionContainingIgnoreCase(String description);

    /**
     * Obtiene el siguiente valor de la secuencia de IDs para restaurantes.
     *
     * @return Mono con el número generado por la secuencia {@code restaurante_seq}
     */
    @Query("SELECT nextval('restaurante_seq')")
    Mono<Long> getNextID();

    /**
     * Busca restaurantes que contengan un tag concreto en su array de tags.
     *
     * @param tag etiqueta por la que filtrar
     * @return Flux con los restaurantes que tienen ese tag
     */
    @Query("SELECT * FROM RESTAURANTES r WHERE :tag = ANY(r.tags)")
    Flux<RestauranteEntity> findByTag(String tag);

}
