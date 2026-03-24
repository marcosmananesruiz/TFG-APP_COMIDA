package com.example.bomboplats.ui.historial;

import java.io.Serializable;

public class PedidoItem implements Serializable {
    private String restauranteId;
    private String bomboId;
    private int cantidad;

    public PedidoItem(String restauranteId, String bomboId, int cantidad) {
        this.restauranteId = restauranteId;
        this.bomboId = bomboId;
        this.cantidad = cantidad;
    }

    public String getRestauranteId() { return restauranteId; }
    public String getBomboId() { return bomboId; }
    public int getCantidad() { return cantidad; }
}
