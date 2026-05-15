package org.dam2.bomboplatsserver.modelo.converter;

import org.dam2.bomboplats.api.Pedido;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * Converter de escritura que transforma un valor del enum {@link Pedido.Estado}
 * a su representación en texto para almacenarlo en base de datos.
 * Se registra como converter de R2DBC para que Spring lo aplique automáticamente
 * al persistir el campo {@code estado} en la tabla de pedidos.
 */
@WritingConverter
public class EstadoWritingConverter implements Converter<Pedido.Estado, String> {
    @Override
    public String convert(Pedido.Estado source) {
        return source.name();
    }
}
