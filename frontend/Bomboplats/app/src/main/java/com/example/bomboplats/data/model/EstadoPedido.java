package com.example.bomboplats.data.model;

import com.example.bomboplats.ui.historial.Pedido;
import java.io.Serializable;

public class EstadoPedido implements Serializable {
    public static final String ESTADO_PREPARACION = "En preparación";
    public static final String ESTADO_CAMINO = "En reparto";
    public static final String ESTADO_ENTREGADO = "Entregado";

    private Pedido pedido;
    private String estado;
    private long timestampCreacion;
    private long timestampEntrega; // Hora estimada de llegada

    public EstadoPedido(Pedido pedido, String estado) {
        this.pedido = pedido;
        this.estado = estado;
        this.timestampCreacion = System.currentTimeMillis();
        // El tiempo estimado de llegada es 6 minutos después de la creación (3 de preparación + 3 de reparto para pruebas)
        this.timestampEntrega = this.timestampCreacion + (6 * 60 * 1000);
    }

    public EstadoPedido(Pedido pedido, String estado, long timestampCreacion) {
        this.pedido = pedido;
        this.estado = estado;
        this.timestampCreacion = timestampCreacion;
        this.timestampEntrega = timestampCreacion + (6 * 60 * 1000);
    }

    public Pedido getPedido() { return pedido; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public long getTimestampCreacion() { return timestampCreacion; }
    public long getTimestampEntrega() { return timestampEntrega; }
}
