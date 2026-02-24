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
@Table("usuarios")
public class UserEntity {

    @EqualsAndHashCode.Include
    // Parece que no hay que poner @GeneratedValue :)
    @Id
    private String id; // Length = 8
    private String nickname; // Length = 32
    private String email; // Length = 254
    private String password; // Length = 60
    private String iconUrl; // Length = 32

}
