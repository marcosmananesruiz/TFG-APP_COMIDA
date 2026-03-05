package com.example.bomboplats.data.model;

public class BomboConCantidad {
    private Bombo bombo;
    private int cantidad;

    public BomboConCantidad(Bombo bombo, int cantidad) {
        this.bombo = bombo;
        this.cantidad = cantidad;
    }

    public Bombo getBombo() { return bombo; }
    public int getCantidad() { return cantidad; }
}
