package org.dam2.bomboplatsserver.modelo.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Table("platos")
public class PlatoEntity {

    @Id
    @EqualsAndHashCode.Include
    private String id; // Length = 8
    private String nombre; // Length = 32
    private String description; // Length = 128
    private String[] tags; // Length = 32
    //Alomejor deberiamos meter array? Depende de las fotos que queramos meter.
    private String iconUrl; // Length = 255
    private String[] possibleModifications; // Length = 32

    private String idRestaurante; //relacion con RestauranteEntity, Length = 8. Borrar los platos no borrara los restaurantes.



}
