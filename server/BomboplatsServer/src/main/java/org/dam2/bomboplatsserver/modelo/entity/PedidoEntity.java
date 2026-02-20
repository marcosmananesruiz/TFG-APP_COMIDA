package org.dam2.bomboplatsserver.modelo.entity;


import lombok.*;
import org.dam2.bomboplats.api.Pedido;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Table("PEDIDOS")
public class PedidoEntity {

    @EqualsAndHashCode.Include
    @Id
    private String id; // Length = 8
    private String idPlato; // Relacion con Plato. Length = 8. Borrar el pedido no borraria el Plato
    private String idUser; // Relacion con User. Length = 8. Borrar el pedido no borraria el User
    private String[] modificaciones; // PostgreSQL soporta arrays en texto y numeros, para almacenar las modificaciones
    private Pedido.Estado estado; // Usa converter Length = 10
    private LocalDateTime entrega;
}
