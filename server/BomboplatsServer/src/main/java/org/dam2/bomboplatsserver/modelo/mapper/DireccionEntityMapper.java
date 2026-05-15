package org.dam2.bomboplatsserver.modelo.mapper;

import org.dam2.bomboplats.api.Direccion;
import org.dam2.bomboplatsserver.modelo.entity.DireccionEntity;
import org.dam2.bomboplatsserver.repo.DireccionRepository;
import org.dam2.bomboplatsserver.service.IDireccionService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Mapper encargado de convertir entre {@link Direccion} (modelo de API)
 * y {@link DireccionEntity} (entidad de base de datos).
 * Al convertir a entidad, recupera de base de datos los IDs de usuario
 * y restaurante asociados a la dirección.
 */
public class DireccionEntityMapper implements EntityMapper<DireccionEntity, Direccion> {

    @Autowired private DireccionRepository repo;


    /**
     * Convierte un {@link Direccion} a su representación como entidad de base de datos.
     * Recupera en paralelo el ID de usuario y el ID de restaurante asociados
     * antes de construir la entidad.
     *
     * @param o Mono con la dirección a convertir
     * @return Mono con la entidad resultante
     */
    @Override
    public Mono<DireccionEntity> map(Mono<Direccion> o) {
        return o.flatMap(direccion -> {

                Mono<String> userIdMono = this.repo.findIdUserById(direccion.getId());
                Mono<String> restauranteIdMono = this.repo.findIdRestauranteById(direccion.getId());

                return Mono.zip(userIdMono, restauranteIdMono).map(tuple -> {
                    DireccionEntity direccionEntity = new DireccionEntity();
                    direccionEntity.setId(direccion.getId());
                    direccionEntity.setPoblacion(direccion.getPoblacion());
                    direccionEntity.setCalle(direccion.getCalle());
                    direccionEntity.setCodigoPostal(direccion.getCodigoPostal());
                    direccionEntity.setPortal(direccion.getPortal());
                    direccionEntity.setPiso(direccion.getPiso());
                    direccionEntity.setIdUser(tuple.getT1());
                    direccionEntity.setIdRestaurante(tuple.getT2());
                    return direccionEntity;
                });
            }
        );
    }

    /**
     * Convierte una {@link DireccionEntity} a su representación como modelo de API.
     * Los IDs de usuario y restaurante no se incluyen en el DTO resultante.
     *
     * @param o Mono con la entidad a convertir
     * @return Mono con la dirección resultante
     */
    @Override
    public Mono<Direccion> unmap(Mono<DireccionEntity> o) {
        return o.map(direccion -> Direccion.builder()
                            .id(direccion.getId())
                            .poblacion(direccion.getPoblacion())
                            .calle(direccion.getCalle())
                            .codigoPostal(direccion.getCodigoPostal())
                            .portal(direccion.getPortal())
                            .piso(direccion.getPiso())
                            .build());
    }

    /**
     * Convierte un flujo de entidades {@link DireccionEntity} a un flujo de objetos {@link Direccion}.
     *
     * @param entities Flux con las entidades a convertir
     * @return Flux con las direcciones resultantes
     */
    public Flux<Direccion> mapFlux(Flux<DireccionEntity> entities) {
        return entities.map(direccionEntity -> Direccion.builder()
                .id(direccionEntity.getId())
                .poblacion(direccionEntity.getPoblacion())
                .calle(direccionEntity.getCalle())
                .codigoPostal(direccionEntity.getCodigoPostal())
                .portal(direccionEntity.getPortal())
                .piso(direccionEntity.getPiso())
                .build()
        );
    }
}
