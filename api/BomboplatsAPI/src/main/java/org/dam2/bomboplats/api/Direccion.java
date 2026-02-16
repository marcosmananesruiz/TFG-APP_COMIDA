package org.dam2.bomboplats.api;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Direccion {

    private String poblacion;
    private String codigoPostal;
    private String calle;
    private int portal;
    private String piso;


    public static Direccion testDirecccion() {
        return new Direccion("Test", "00001", "Calle Testeo", 0, "1ºA");
    }
}
