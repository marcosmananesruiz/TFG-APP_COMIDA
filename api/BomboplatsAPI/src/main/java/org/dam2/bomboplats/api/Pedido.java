package org.dam2.bomboplats.api;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pedido {

    private long id;
    private Plato plato;
    private User user;
    private List<String> modifications;
    private Estado estado;
    private LocalDateTime entrega;


    public enum Estado {
        PREPARING,
        DELIVERING,
        DELIVERED
    }
}
