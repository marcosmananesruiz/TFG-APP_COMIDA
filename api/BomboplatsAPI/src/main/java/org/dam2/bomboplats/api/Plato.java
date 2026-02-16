package org.dam2.bomboplats.api;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Plato {

    @EqualsAndHashCode.Include
    private long id;
    private String nombre;
    private String description;
    private String iconUrl;
    private List<String> tags;
    private List<String> possibleModifications;

}
