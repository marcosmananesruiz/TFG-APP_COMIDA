package org.dam2.bomboplats.api;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Direccion {

    @EqualsAndHashCode.Include
    private String id;
    private String poblacion;
    private String codigoPostal;
    private String calle;
    private int portal;
    private String piso;


    public static Direccion testDirecccion() {
        return new Direccion("D00000","Test", "00001", "Calle Testeo", 0, "1ºA");
    }
}
