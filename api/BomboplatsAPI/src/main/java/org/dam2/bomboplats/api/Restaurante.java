package org.dam2.bomboplats.api;


import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Restaurante {

    @EqualsAndHashCode.Include
    private String id;
    private String nombre;
    private String description;
    private String iconUrl;
    private List<String> tags;
    private Set<Plato> platos;
    private List<Direccion> direcciones;


}
