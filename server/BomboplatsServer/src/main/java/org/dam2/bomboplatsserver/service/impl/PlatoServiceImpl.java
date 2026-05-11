package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplats.api.Plato;
import org.dam2.bomboplatsserver.modelo.entity.PlatoEntity;
import org.dam2.bomboplatsserver.repo.PlatoRepository;
import org.dam2.bomboplatsserver.service.IPedidoService;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Implementación del servicio de platos.
 * Gestiona las operaciones CRUD sobre platos, incluyendo
 * la conversión entre entidad y DTO, y la generación de IDs únicos.
 */
@Service
public class PlatoServiceImpl implements IPlatoService {

    @Autowired private PlatoRepository repo;
    @Autowired private R2dbcEntityTemplate template;
    @Autowired @Lazy private IPedidoService pedidoService;
    public static Logger LOGGER = LoggerFactory.getLogger(PlatoServiceImpl.class);
    //Prefijo que usamos para generar los IDs de los platos en la bbdd
    private final String UNIQUE_CHAR = "T";

    private String[] toStringArray(Object[] input) {
        if (input == null) return new String[0];
        return Arrays.stream(input)
                .map(o -> o != null ? o.toString() : null)
                .toArray(String[]::new);
    }

    /**
     * Convierte una {@link PlatoEntity} al DTO {@link Plato}.
     * Gestiona la conversión de arrays genéricos a listas de Strings.
     *
     * @param entity entidad a convertir
     * @return DTO resultante
     */
    private Plato toDTO(PlatoEntity entity) {
        String[] tags = entity.getTags() instanceof Object[]
                ? toStringArray((Object[]) entity.getTags())
                : entity.getTags();
        String[] mods = entity.getPossibleModifications() instanceof Object[]
                ? toStringArray((Object[]) entity.getPossibleModifications())
                : entity.getPossibleModifications();

        return Plato.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .description(entity.getDescription())
                .iconUrl(entity.getIconUrl())
                .tags(tags != null ? Arrays.asList(tags) : List.of())
                .possibleModifications(mods != null ? Arrays.asList(mods) : List.of())
                .precio(entity.getPrecio())
                .build();
    }

    /**
     * Convierte un DTO {@link Plato} a su entidad {@link PlatoEntity}.
     * Las listas de tags y modificaciones se convierten a arrays para su almacenamiento.
     *
     * @param plato DTO a convertir
     * @return entidad resultante
     */
    private PlatoEntity toEntity(Plato plato) {
        return PlatoEntity.builder()
                .id(plato.getId())
                .nombre(plato.getNombre())
                .description(plato.getDescription())
                .iconUrl(plato.getIconUrl())
                .tags(plato.getTags() != null ? plato.getTags().toArray(new String[0]) : new String[0])
                .possibleModifications(plato.getPossibleModifications() != null
                        ? plato.getPossibleModifications().toArray(new String[0])
                        : new String[0])
                .precio(plato.getPrecio())
                .build();
    }

    /**
     * Busca un plato por su ID.
     *
     * @param id identificador del plato
     * @return Mono con el plato, o vacío si no existe
     */
    @Override
    public Mono<Plato> findById(String id) {
        return this.repo.findById(id).map(this::toDTO);
    }

    /**
     * Devuelve todos los platos del sistema.
     *
     * @return Flux con todos los platos
     */
    @Override
    public Flux<Plato> findAll() {
        return this.repo.findAll().map(this::toDTO);
    }

    /**
     * Registra un nuevo plato generando un ID único con el formato "T00001".
     * En caso de error, lo registra en el log y devuelve un Mono vacío.
     *
     * @param plato datos del plato a registrar
     * @return Mono con el plato creado, o vacío si hubo un error
     */
    @Override
    public Mono<Plato> register(Plato plato) {
        PlatoEntity entity = toEntity(plato);
        return this.repo.getNextID()
                .map(idNumber -> String.format("%s%05d", UNIQUE_CHAR, idNumber))
                .flatMap(id -> {
                    entity.setId(id);
                    return this.template.insert(PlatoEntity.class).using(entity);
                })
                .map(this::toDTO)
                .onErrorResume(e -> {
                    LOGGER.error("Error al registrar plato: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

    /**
     * Actualiza los datos de un plato existente.
     * Conserva el ID de restaurante que tenía asignado previamente.
     *
     * @param plato datos actualizados del plato
     * @return {@code true} si se actualizó correctamente, {@code false} si no existía
     */
    @Override
    public Mono<Boolean> update(Plato plato) {
        PlatoEntity entity = toEntity(plato);
        return this.repo.findById(entity.getId())
                .flatMap(existing -> {
                    entity.setIdRestaurante(existing.getIdRestaurante());
                    return this.repo.save(entity)
                            .doOnNext(u -> LOGGER.info("Plato con ID {} actualizado", u.getId()))
                            .thenReturn(true);
                }).defaultIfEmpty(false);
    }

    /**
     * Elimina un plato por su IDr.
     * Si el plato tiene pedidos asociados, los elimina antes de borrarlo.
     *
     * @param id identificador del plato a eliminar
     * @return {@code true} si se eliminó correctamente, {@code false} si no existía
     */
    @Override
    public Mono<Boolean> deletePlatoById(String id) {
        return this.repo.findById(id)
                .flatMap(exists ->
                        pedidoService.existsByPlatoId(id)
                                .flatMap(hayPedidos -> {
                                    if (hayPedidos) {
                                        return pedidoService.findByPlatoId(id)
                                                .flatMap(pedido -> pedidoService.deletePedidoById(pedido.getId()))
                                                .then(this.repo.deleteById(id));
                                    } else {
                                        return this.repo.deleteById(id);
                                    }
                                })
                                .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    /**
     * Devuelve todos los platos pertenecientes a un restaurante concreto.
     * Recoge el id del restaurante y muestra los platos asoaciados.
     *
     * @param idRestaurante identificador del restaurante
     * @return Flux con los platos de ese restaurante
     */
    @Override
    public Flux<Plato> findByIdRestaurante(String idRestaurante) {
        return this.repo.findByIdRestaurante(idRestaurante).map(this::toDTO);
    }

    /**
     * Busca platos cuyo nombre contenga el texto indicado, sin distinguir mayúsculas.
     *
     * @param nombre texto a buscar en el nombre del plato
     * @return Flux con los platos que coinciden
     */
    @Override
    public Flux<Plato> findByNombreContaining(String nombre) {
        return this.repo.findByNombreContainingIgnoreCase(nombre).map(this::toDTO);
    }

    /**
     * Busca platos de un restaurante cuyo nombre contenga el texto indicado, sin distinguir mayúsculas.
     *
     * @param idRestaurante identificador del restaurante
     * @param nombre        texto a buscar en el nombre del plato
     * @return Flux con los platos que coinciden con ambos criterios
     */
    @Override
    public Flux<Plato> findByIdRestauranteAndNombreContaining(String idRestaurante, String nombre) {
        return this.repo.findByIdRestauranteAndNombreContainingIgnoreCase(idRestaurante, nombre).map(this::toDTO);
    }

    /**
     * Busca platos que tengan asignado un tag concreto.
     *
     * @param tag etiqueta por la que filtrar
     * @return Flux con los platos que tienen ese tag
     */
    @Override
    public Flux<Plato> findByTag(String tag) {
        return this.repo.findByTag(tag).map(this::toDTO);
    }

    /**
     * Registra un nuevo plato asociándolo directamente a un restaurante por su ID.
     * Genera un ID único con el formato "T00001".
     * En caso de error, lo registra en el log y devuelve un Mono vacío.
     *
     * @param plato         datos del plato a registrar
     * @param idRestaurante identificador del restaurante al que pertenece
     * @return Mono con el plato creado, o vacío si hubo un error
     */
    @Override
    public Mono<Plato> registerConRestaurante(Plato plato, String idRestaurante) {
        PlatoEntity entity = toEntity(plato);
        entity.setIdRestaurante(idRestaurante);
        return this.repo.getNextID()
                .map(idNumber -> String.format("%s%05d", UNIQUE_CHAR, idNumber))
                .flatMap(id -> {
                    entity.setId(id);
                    return this.template.insert(PlatoEntity.class).using(entity);
                })
                .map(this::toDTO)
                .onErrorResume(e -> {
                    LOGGER.error("Error al registrar plato con restaurante: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

}