package org.dam2.bomboplatsserver.modelo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Table("direcciones")
public class DireccionEntity {

    @EqualsAndHashCode.Include
    @Id
    private String id; // Length = 8

    private String poblacion; // Length = 24
    private String codigoPostal; // Length = 5
    private String calle; // Length = 32
    private int portal;
    private String piso; // Length = 8
    @Column("id_usuario")
    private String idUser; // Relacion con UserEntity, Length = 8. Borrar las direcciones no borrara los usuarios
    private String idRestaurante; // Relacion con RestauranteEntity, Length = 8. Borrar las direcciones no borrara los restaurantes
}
