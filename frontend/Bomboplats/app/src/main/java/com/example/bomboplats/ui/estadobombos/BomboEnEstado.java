package com.example.bomboplats.ui.estadobombos;

import com.example.bomboplats.data.model.Bombo;
import java.io.Serializable;

public class BomboEnEstado implements Serializable {
    public enum Estado {
        PREPARACION("En preparación"),
        ENVIO("En envío"),
        RECIBIDO("Recibido");

        private final String texto;
        Estado(String texto) { this.texto = texto; }
        public String getTexto() { return texto; }
    }

    private String id;
    private Bombo bombo;
    private Estado estado;
    private long tiempoUltimoCambio;

    public BomboEnEstado(String id, Bombo bombo) {
        this.id = id;
        this.bombo = bombo;
        this.estado = Estado.PREPARACION;
        this.tiempoUltimoCambio = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public Bombo getBombo() { return bombo; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { 
        this.estado = estado; 
        this.tiempoUltimoCambio = System.currentTimeMillis();
    }
    public long getTiempoUltimoCambio() { return tiempoUltimoCambio; }
}
