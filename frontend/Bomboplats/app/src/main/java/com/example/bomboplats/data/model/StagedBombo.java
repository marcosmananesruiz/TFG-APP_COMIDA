package com.example.bomboplats.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StagedBombo {

    private static final java.util.concurrent.atomic.AtomicInteger ID_COUNTER =
            new java.util.concurrent.atomic.AtomicInteger(0);
    private final int id;
    private Bombo bombo;
    private int cantidad;
    private List<String> modificaciones;

    public StagedBombo(Bombo bombo, int cantidad, List<String> modificaciones) {
        this.id = ID_COUNTER.incrementAndGet();
        this.bombo = bombo;
        this.cantidad = cantidad;
        // IMPORTANTE: Creamos una copia nueva para romper la referencia de memoria.
        // Esto evita que si se cambian las modificaciones en la pantalla de detalle, 
        // afecten a los platos que ya están guardados en el carrito.
        this.modificaciones = modificaciones != null ? new ArrayList<>(modificaciones) : new ArrayList<>();
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
        // También usamos una copia aquí por seguridad
        this.modificaciones = modificaciones != null ? new ArrayList<>(modificaciones) : new ArrayList<>();
    }

    public int getId() {
        return id;
    }
}
