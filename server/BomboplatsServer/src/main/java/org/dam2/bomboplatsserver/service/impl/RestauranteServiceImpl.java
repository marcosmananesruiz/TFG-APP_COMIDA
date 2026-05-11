package org.dam2.bomboplatsserver.service.impl;

import org.dam2.bomboplats.api.Restaurante;
import org.dam2.bomboplatsserver.modelo.entity.RestauranteEntity;
import org.dam2.bomboplatsserver.repo.RestauranteRepository;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.dam2.bomboplatsserver.service.IPlatoService;
import org.dam2.bomboplatsserver.service.IRestauranteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Implementación del servicio de restaurantes.
 * Gestiona las operaciones CRUD sobre restaurantes, incluyendo
 * la conversión entre entidad y DTO, y la generación de IDs únicos.
 */
@Service
public class RestauranteServiceImpl implements IRestauranteService {

    @Autowired private RestauranteRepository repo;
    @Autowired private R2dbcEntityTemplate template;
    //@Autowired private RestauranteEntityMapper mapper;
    @Autowired private IPlatoService platoService;
    @Autowired private IDireccionService direccionService;

    public static Logger LOGGER = LoggerFactory.getLogger(RestauranteServiceImpl.class);
    //prefijo que usamos para generar los IDs de los restaurantes en la bbdd
    private final String UNIQUE_CHAR = "R";

    /**
     * Añade a una entidad {@link RestauranteEntity} sus platos y direcciones,
     * construyendo el DTO {@link Restaurante} completo de forma reactiva.
     * Recupera ambas listas en paralelo antes de ensamblar el objeto final.
     *
     * @param entity entidad del restaurante a enriquecer
     * @return Mono con el restaurante completo incluyendo platos y direcciones
     */
    private Mono<Restaurante> enrich(RestauranteEntity entity) {
        Mono<List<org.dam2.bomboplats.api.Plato>> platosMono = platoService
                .findByIdRestaurante(entity.getId())
                .collectList();
                //.doOnNext(platos -> LOGGER.info("Platos encontrados para {}: {}", entity.getId(), platos.size()));

        Mono<List<org.dam2.bomboplats.api.Direccion>> direccionesMono = direccionService
                .getDireccionesOfRestaurante(entity.getId())
                .collectList();
                //.doOnNext(dirs -> LOGGER.info("Direcciones encontradas para {}: {}", entity.getId(), dirs.size()));

        return Mono.zip(platosMono, direccionesMono)
                //.doOnSuccess(t -> LOGGER.info("Enrich completado para {}", entity.getId()))
                .map(tuple -> {
                    String[] tags = entity.getTags() instanceof Object[]
                            ? toStringArray((Object[]) entity.getTags())
                            : entity.getTags();
                    String[] iconUrls = entity.getIconUrl() instanceof Object[]
                            ? toStringArray((Object[]) entity.getIconUrl())
                            : entity.getIconUrl();

                    return Restaurante.builder()
                            .id(entity.getId())
                            .nombre(entity.getNombre())
                            .description(entity.getDescription())
                            .tags(tags != null ? java.util.Arrays.asList(tags) : java.util.List.of())
                            .iconUrls(iconUrls != null ? java.util.Arrays.asList(iconUrls) : java.util.List.of())
                            .rating(entity.getRating())
                            .platos(new java.util.HashSet<>(tuple.getT1()))
                            .direcciones(tuple.getT2())
                            .build();
                });
    }

    private String[] toStringArray(Object[] input) {
        if (input == null) return new String[0];
        return java.util.Arrays.stream(input)
                .map(o -> o != null ? o.toString() : null)
                .toArray(String[]::new);
    }

    /**
     * Convierte un DTO {@link Restaurante} a su entidad {@link RestauranteEntity}.
     * Las listas de tags e iconos se convierten a arrays para su almacenamiento.
     * Los platos y direcciones no se incluyen, ya que se gestionan por sus propios servicios.
     *
     * @param restaurante DTO a convertir
     * @return entidad resultante
     */
    private RestauranteEntity toEntity(Restaurante restaurante) {
        return RestauranteEntity.builder()
                .id(restaurante.getId())
                .nombre(restaurante.getNombre())
                .description(restaurante.getDescription())
                .tags(restaurante.getTags() != null
                        ? restaurante.getTags().toArray(new String[0])
                        : new String[0])
                .iconUrl(restaurante.getIconUrls() != null
                        ? restaurante.getIconUrls().toArray(new String[0])
                        : new String[0])
                .rating(restaurante.getRating())
                .build();
    }

    /**
     * Busca un restaurante por su identificador y lo enriquece con sus platos y direcciones.
     *
     * @param id identificador del restaurante
     * @return Mono con el restaurante completo, o vacío si no existe
     */
    @Override
    public Mono<Restaurante> findById(String id) {
        return this.repo.findById(id).flatMap(this::enrich);
    }

    /**
     * Devuelve todos los restaurantes del sistema, cada uno enriquecido con sus platos y direcciones.
     *
     * @return Flux con todos los restaurantes
     */
    @Override
    public Flux<Restaurante> findAll() {
        return this.repo.findAll().flatMap(this::enrich);
    }


    /**
     * Registra un nuevo restaurante generando un ID único con el formato "R{número}".
     * En caso de error, lo registra en el log y devuelve un Mono vacío.
     *
     * @param restaurante datos del restaurante a registrar
     * @return Mono con el restaurante creado y enriquecido, o vacío si hubo un error
     */
    @Override
    public Mono<Restaurante> register(Restaurante restaurante) {
        RestauranteEntity entity = toEntity(restaurante);
        return this.repo.getNextID()
                .map(idNumber -> this.UNIQUE_CHAR + idNumber)
                .flatMap(id -> {
                    entity.setId(id);
                    return this.template.insert(RestauranteEntity.class).using(entity);
                })
                .flatMap(this::enrich)
                .onErrorResume(e -> {
                    LOGGER.error("Error al registrar restaurante: {}", e.getMessage(), e);
                    return Mono.empty();
                });
    }

    /**
     * Actualiza los datos de un restaurante existente.
     *
     * @param restaurante datos actualizados del restaurante
     * @return {@code true} si se actualizó correctamente, {@code false} si no existía
     */
    @Override
    public Mono<Boolean> update(Restaurante restaurante) {
        RestauranteEntity entity = toEntity(restaurante);
        return this.repo.findById(entity.getId())
                .flatMap(existing -> this.repo.save(entity)
                        .doOnNext(u -> LOGGER.info("Restaurante con ID {} actualizado", u.getId()))
                        .thenReturn(true)
                ).defaultIfEmpty(false);
    }

    /**
     * Elimina un restaurante y todos sus platos y direcciones asociados.
     * Los platos y direcciones se borran en paralelo antes de eliminar el restaurante.
     *
     * @param id identificador del restaurante a eliminar
     * @return {@code true} si se eliminó correctamente, {@code false} si no existía
     */
    @Override
    public Mono<Boolean> deleteRestauranteById(String id) {
        return this.repo.findById(id)
                .flatMap(exists -> {
                    Mono<Void> borrarPlatos = platoService.findByIdRestaurante(id)
                            .flatMap(plato -> platoService.deletePlatoById(plato.getId()))
                            .then();

                    Mono<Void> borrarDirecciones = direccionService.getDireccionesOfRestaurante(id)
                            .flatMap(dir -> direccionService.deleteDireccionByID(dir.getId()))
                            .then();

                    return Mono.when(borrarPlatos, borrarDirecciones)
                            .then(this.repo.deleteById(id))
                            .doOnSuccess(d -> LOGGER.info("Restaurante con ID {} y sus platos y direcciones eliminados", id))
                            .thenReturn(true);
                }).defaultIfEmpty(false);
    }

    /**
     * Busca restaurantes cuyo nombre coincida exactamente con el indicado.
     *
     * @param nombre nombre exacto a buscar
     * @return Flux con los restaurantes que coinciden
     */
    @Override
    public Flux<Restaurante> findByNombre(String nombre) {
        return this.repo.findByNombre(nombre).flatMap(this::enrich);
    }
    /**
     * Busca restaurantes cuyo nombre contenga el texto indicado, sin distinguir mayúsculas.
     *
     * @param nombre texto a buscar en el nombre del restaurante
     * @return Flux con los restaurantes que coinciden
     */
    @Override
    public Flux<Restaurante> findByNombreContaining(String nombre) {
        return this.repo.findByNombreContainingIgnoreCase(nombre).flatMap(this::enrich);
    }

    /**
     * Busca restaurantes cuya descripción contenga el texto indicado, sin distinguir mayúsculas.
     *
     * @param description texto a buscar en la descripción
     * @return Flux con los restaurantes que coinciden
     */
    @Override
    public Flux<Restaurante> findByDescriptionContaining(String description) {
        return this.repo.findByDescriptionContainingIgnoreCase(description).flatMap(this::enrich);
    }
    /**
     * Busca restaurantes que tengan asignado un tag concreto.
     *
     * @param tag etiqueta por la que filtrar
     * @return Flux con los restaurantes que tienen ese tag
     */
    @Override
    public Flux<Restaurante> findByTag(String tag) {
        return this.repo.findByTag(tag).flatMap(this::enrich);
    }
}