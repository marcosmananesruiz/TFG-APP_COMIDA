package org.dam2.bomboplatsserver.modelo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Table("DIRECCIONES")
public class DireccionEntity {

    @EqualsAndHashCode.Include
    @Id
    private String id; // Length = 8

    private String poblacion; // Length = 24
    private String codigoPostal; // Length = 5
    private String calle; // Length = 32
    private int portal;
    private String piso; // Length = 8

    // Con el @OneToMany de UserEntity se habría creado este campo en la tabla para
    // que cada usuario pudiera tener multiples direcciones, pero ahora tenemos que hacerlo asi
    // en la que nosotros mismos colocamos la relacion

    // Esto si tenemos dudas, se lo pasamos a la IA y que haga como se verian las relaciones

    private String idResidente; // Relacion con UserEntity y RestauranteEntity, Length = 8. Borrar las direcciones  no borrara los usuarios ni restaurantes
}
