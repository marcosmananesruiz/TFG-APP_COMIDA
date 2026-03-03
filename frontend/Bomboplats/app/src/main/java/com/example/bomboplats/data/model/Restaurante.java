package com.example.bomboplats.data.model;

import java.util.List;

public class Restaurante {
    private String id;
    private String nombre;
    private String descripcion;
    private float estrellas;
    private String rangoPrecio;
    private List<String> etiquetas;

    public Restaurante(String id, String nombre, String descripcion, float estrellas, String rangoPrecio, List<String> etiquetas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estrellas = estrellas;
        this.rangoPrecio = rangoPrecio;
        this.etiquetas = etiquetas;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public float getEstrellas() { return estrellas; }
    public String getRangoPrecio() { return rangoPrecio; }
    public List<String> getEtiquetas() { return etiquetas; }
}
