package org.dam2.bomboplatsserver.modelo.converter;

import org.dam2.bomboplats.api.Pedido;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class EstadoWritingConverter implements Converter<Pedido.Estado, String> {
    @Override
    public String convert(Pedido.Estado source) {
        return source.name();
    }
}
