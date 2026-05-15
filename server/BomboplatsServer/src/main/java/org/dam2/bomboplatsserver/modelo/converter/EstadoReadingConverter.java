package org.dam2.bomboplatsserver.modelo.converter;

import org.dam2.bomboplats.api.Pedido;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * Converter de lectura que transforma un valor {@link String} de la base de datos
 * al enum {@link Pedido.Estado}.
 * Se registra como converter de R2DBC para que Spring lo aplique automáticamente
 * al leer el campo {@code estado} de la tabla de pedidos.
 */
@ReadingConverter
public class EstadoReadingConverter implements Converter<String, Pedido.Estado> {
    @Override
    public Pedido.Estado convert(String source) {
        return Pedido.Estado.valueOf(source);
    }
}
