package com.example.bomboplats.data.model;

import com.example.bomboplats.ui.historial.Pedido;
import java.io.Serializable;

public class EstadoPedido implements Serializable {
    private Pedido pedido;
    private String estado;
    private long timestampCreacion; // El "reloj" interno del pedido

    public EstadoPedido(Pedido pedido, String estado) {
        this.pedido = pedido;
        this.estado = estado;
        this.timestampCreacion = System.currentTimeMillis();
    }

    // Constructor para cargar desde disco
    public EstadoPedido(Pedido pedido, String estado, long timestampCreacion) {
        this.pedido = pedido;
        this.estado = estado;
        this.timestampCreacion = timestampCreacion;
    }

    public Pedido getPedido() { return pedido; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public long getTimestampCreacion() { return timestampCreacion; }
}
