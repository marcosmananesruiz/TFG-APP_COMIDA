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

    @Id
    private String id;

    private String poblacion;
    private String codigoPostal;
    private String calle;
    private int portal;
    private String piso;
    // ID del User/Restaurante de esta dirección

    // Con el @OneToMany de UserEntity se habría creado este campo en la tabla para
    // que cada usuario pudiera tener multiples direcciones, pero ahora tenemos que hacerlo asi
    // en la que nosotros mismos colocamos la relacion
    private String idResidente;
}
