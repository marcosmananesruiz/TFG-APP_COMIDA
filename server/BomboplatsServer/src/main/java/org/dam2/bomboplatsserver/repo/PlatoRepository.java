package org.dam2.bomboplatsserver.repo;

import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo para la entidad {@link PlatoEntity}.
 * Extiende {@link ReactiveCrudRepository} con consultas adicionales
 * para búsquedas por restaurante, nombre y tags.
 */
public interface PlatoRepository extends ReactiveCrudRepository<PlatoEntity, String> {

    /**
     * Obtiene el siguiente valor de la secuencia de IDs para platos.
     *
     * @return Mono con el número generado por la secuencia {@code plato_seq}
     */
    @Query("SELECT nextval('plato_seq')")
    Mono<Long> getNextID();

    /**
     * Devuelve todos los platos asociados a un restaurante concreto.
     *
     * @param idRestaurante identificador del restaurante
     * @return Flux con los platos de ese restaurante
     */
    Flux<PlatoEntity> findByIdRestaurante(String idRestaurante);

    /**
     * Busca platos cuyo nombre contenga el texto indicado, sin distinguir mayúsculas.
     *
     * @param nombre texto a buscar en el nombre del plato
     * @return Flux con los platos que coinciden
     */
    Flux<PlatoEntity> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca platos de un restaurante concreto cuyo nombre contenga el texto indicado,
     * sin distinguir mayúsculas.
     *
     * @param idRestaurante identificador del restaurante
     * @param nombre        texto a buscar en el nombre del plato
     * @return Flux con los platos que coinciden con ambos criterios
     */
    Flux<PlatoEntity> findByIdRestauranteAndNombreContainingIgnoreCase(String idRestaurante, String nombre);

    /**
     * Busca platos que contengan un tag concreto en su array de tags.
     *
     * @param tag etiqueta por la que filtrar
     * @return Flux con los platos que tienen ese tag
     */
    @Query("SELECT * FROM PLATOS p WHERE :tag = ANY(p.tags)")
    Flux<PlatoEntity> findByTag(String tag);

}