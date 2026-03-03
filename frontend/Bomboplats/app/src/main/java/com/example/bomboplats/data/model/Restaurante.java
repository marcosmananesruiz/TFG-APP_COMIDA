package com.example.bomboplats.data.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurante {
    private String id;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private float estrellas;
    private String rangoPrecio;
    private List<String> etiquetas;
    private List<Integer> fotos; // Usaremos IDs de recursos por ahora para el ejemplo

    public Restaurante(String id, String nombre, String descripcion, float estrellas, String rangoPrecio, List<String> etiquetas) {
        this(id, nombre, descripcion, "Ubicación no disponible", estrellas, rangoPrecio, etiquetas, new ArrayList<>());
    }

    public Restaurante(String id, String nombre, String descripcion, String ubicacion, float estrellas, String rangoPrecio, List<String> etiquetas, List<Integer> fotos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.estrellas = estrellas;
        this.rangoPrecio = rangoPrecio;
        this.etiquetas = etiquetas;
        this.fotos = fotos;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getUbicacion() { return ubicacion; }
    public float getEstrellas() { return estrellas; }
    public String getRangoPrecio() { return rangoPrecio; }
    public List<String> getEtiquetas() { return etiquetas; }
    public List<Integer> getFotos() { return fotos; }

    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setFotos(List<Integer> fotos) { this.fotos = fotos; }
}
