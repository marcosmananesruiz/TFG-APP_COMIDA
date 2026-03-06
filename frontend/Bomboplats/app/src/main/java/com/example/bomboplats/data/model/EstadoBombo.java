package com.example.bomboplats.data.model;

public class EstadoBombo {
    private BomboConCantidad bomboConCantidad;
    private String estado;

    public EstadoBombo(BomboConCantidad bomboConCantidad, String estado) {
        this.bomboConCantidad = bomboConCantidad;
        this.estado = estado;
    }

    public BomboConCantidad getBomboConCantidad() {
        return bomboConCantidad;
    }

    public void setBomboConCantidad(BomboConCantidad bomboConCantidad) {
        this.bomboConCantidad = bomboConCantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
