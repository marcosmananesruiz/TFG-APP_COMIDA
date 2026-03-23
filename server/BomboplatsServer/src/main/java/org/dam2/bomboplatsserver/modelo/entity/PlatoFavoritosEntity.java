package org.dam2.bomboplatsserver.modelo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Table("platos_favoritos")
public class PlatoFavoritosEntity {
    // Relacion Many to Many
    @EqualsAndHashCode.Include
    @Id
    private Long id; // Autogenerado aparte

    private String userId; // Relacion con UserEntity. Lenght = 8
    private String platoId; // Relacion con PlatoEntity. Length = 8
}
