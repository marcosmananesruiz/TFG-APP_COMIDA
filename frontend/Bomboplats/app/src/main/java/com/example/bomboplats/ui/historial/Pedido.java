package com.example.bomboplats.ui.historial;

import com.example.bomboplats.data.model.BomboConCantidad;
import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {
    private String id;
    private String fecha;
    private List<BomboConCantidad> items;
    private double total;
    private String direccion;

    public Pedido(String id, String fecha, List<BomboConCantidad> items, double total, String direccion) {
        this.id = id;
        this.fecha = fecha;
        this.items = items;
        this.total = total;
        this.direccion = direccion;
    }

    public String getId() { return id; }
    public String getFecha() { return fecha; }
    public List<BomboConCantidad> getItems() { return items; }
    public double getTotal() { return total; }
    public String getDireccion() { return direccion; }
}
