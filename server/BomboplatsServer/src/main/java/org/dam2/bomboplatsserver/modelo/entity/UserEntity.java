package org.dam2.bomboplatsserver.modelo.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

// Con datos reactivos no se pone @Entity!
@Table("USUARIOS")
public class UserEntity {

    @EqualsAndHashCode.Include
    // Parece que no hay que poner @GeneratedValue :)
    @Id
    private String id; // Length = 8
    private String nickname; // Length = 32
    private String email; // Length = 254
    private String password; // Length = 60
    private String iconUrl; // Length = 32

    // Por usar reactividad, no estamos usando el JPA que conocemos por lo que hemos perdido
    // las anotaciones @OneToOne, @ManyToOne...
    // Tenemos que hacer las relaciones TAL CUAL estarian en la base de datos.
    // Ejemplo en DireccionEntity

    //private List<DireccionEntity> direcciones;

}
