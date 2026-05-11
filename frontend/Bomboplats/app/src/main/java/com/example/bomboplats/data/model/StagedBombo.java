package com.example.bomboplats.data.model;

import java.util.List;
import java.util.Random;

public class StagedBombo {

    private final int id;
    private Bombo bombo;
    private int cantidad;
    private List<String> modificaciones;

    public StagedBombo(Bombo bombo, int cantidad, List<String> modificaciones) {
        this.id = new Random().nextInt(100000);
        this.bombo = bombo;
        this.cantidad = cantidad;
        this.modificaciones = modificaciones;
    }

    public Bombo getBombo() {
        return bombo;
    }

    public void setBombo(Bombo bombo) {
        this.bombo = bombo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public List<String> getModificaciones() {
        return modificaciones;
    }

    public void setModificaciones(List<String> modificaciones) {
        this.modificaciones = modificaciones;
    }

    public int getId() {
        return id;
    }
}
