package org.dam2.bomboplatsserver.modelo.converter;

import org.dam2.bomboplats.api.Pedido;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class EstadoReadingConverter implements Converter<String, Pedido.Estado> {
    @Override
    public Pedido.Estado convert(String source) {
        return Pedido.Estado.valueOf(source);
    }
}
