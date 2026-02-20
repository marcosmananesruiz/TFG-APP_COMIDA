package org.dam2.bomboplatsserver.modelo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Table("RESTAURANTES")
public class RestauranteEntity {

    @Id
    @EqualsAndHashCode.Include
    private String id; // Length = 8
    private String nombre; // Length = 32

    private String[] tags; // Length = 16
    private String iconUrl; // Length = 255
    private String description; // Length = 64


}
