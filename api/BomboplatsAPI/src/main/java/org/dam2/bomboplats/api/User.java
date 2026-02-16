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
    private long id;
    private String nickname;
    private String email;
    private String iconUrl;
    private Set<Direccion> direcciones;


    public static User testUser() {
        return new User(0, "Usuario Test", "test@bomboplats.es", "icon/fail", Set.of(Direccion.testDirecccion()));
    }

}
