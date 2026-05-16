package com.example.bomboplats.ui.historial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase PedidoItem.
 */
public class PedidoItem implements Serializable {
    private String restauranteId;
    private String bomboId;
    private int cantidad;
    private List<String> modificaciones;

    // Constructor
    public PedidoItem(String restauranteId, String bomboId, int cantidad, List<String> modificaciones) {
        this.restauranteId = restauranteId;
        this.bomboId = bomboId;
        this.cantidad = cantidad;
        this.modificaciones = modificaciones != null ? new ArrayList<>(modificaciones) : new ArrayList<>();
    }

    // Getters
    public String getRestauranteId() { return restauranteId; }
    public String getBomboId() { return bomboId; }
    public int getCantidad() { return cantidad; }
    public List<String> getModificaciones() { return modificaciones; }
}
