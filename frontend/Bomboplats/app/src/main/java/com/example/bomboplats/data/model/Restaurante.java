package com.example.bomboplats.data.model;

import java.util.List;

public class Restaurante {
    private String id;
    private String nombre;
    private String descripcion;
    private float estrellas;
    private String rangoPrecio;
    private List<String> etiquetas;
    private String ubicacion;
    private List<String> fotosCarrusel;

    public Restaurante(String id, String nombre, String descripcion, float estrellas, String rangoPrecio, List<String> etiquetas, String ubicacion, List<String> fotosCarrusel) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estrellas = estrellas;
        this.rangoPrecio = rangoPrecio;
        this.etiquetas = etiquetas;
        this.ubicacion = ubicacion;
        this.fotosCarrusel = fotosCarrusel;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public float getEstrellas() { return estrellas; }
    public String getRangoPrecio() { return rangoPrecio; }
    public List<String> getEtiquetas() { return etiquetas; }
    public String getUbicacion() { return ubicacion; }
    public List<String> getFotosCarrusel() { return fotosCarrusel; }
}
