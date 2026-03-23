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

    @EqualsAndHashCode.Include
    @Id
    private long id;

    private String userId;
    private String platoId;
}
