package org.dam2.bomboplats.api;


import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @EqualsAndHashCode.Include
    private String id;
    private String nickname;
    private String email;
    private String iconUrl;
    private Set<Direccion> direcciones;
    private Set<Plato> platosFavoritos;

}
