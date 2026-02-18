package org.dam2.bomboplatsserver.modelo.entity;


import lombok.*;
import org.dam2.bomboplats.api.Pedido;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Table("PEDIDOS")
public class PedidoEntity {

    @Id
    private String id;
    private Long idPlato;
    private Long idUsuario;
    // PostgreSQL soporta arrays en texto y numeros, para almacenar las modificaciones
    private String[] modificaciones;
    private Pedido.Estado estado;
    private LocalDateTime entrega;
}
