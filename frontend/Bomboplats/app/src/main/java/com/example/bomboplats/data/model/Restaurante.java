package com.example.bomboplats.data.model;

import java.util.ArrayList;
import java.util.List;

public class Restaurante {
    private String id;
    private String nombre;
    private String descripcion;
    private List<String> etiquetas;
    private List<String> fotos;
    private String ubicacion;
    private float valoracion;
    private String rangoPrecio;
    private List<Bombo> menu;

    public Restaurante(String id, String nombre, String descripcion, List<String> etiquetas, List<String> fotos, String ubicacion, float valoracion, String rangoPrecio, List<Bombo> menu) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.etiquetas = etiquetas != null ? etiquetas : new ArrayList<>();
        this.fotos = fotos != null ? fotos : new ArrayList<>();
        this.ubicacion = ubicacion;
        this.valoracion = valoracion;
        this.rangoPrecio = rangoPrecio;
        this.menu = menu != null ? menu : new ArrayList<>();
    }

    // Constructor simplificado para conversiones desde la API
    public Restaurante(String id, String nombre, String descripcion, String iconUrl, List<String> etiquetas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.etiquetas = etiquetas != null ? etiquetas : new ArrayList<>();
        this.fotos = new ArrayList<>();
        if (iconUrl != null) this.fotos.add(iconUrl);
        this.menu = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public List<String> getEtiquetas() { return etiquetas; }
    public List<String> getFotos() { return fotos; }
    public String getUbicacion() { return ubicacion; }
    public float getValoracion() { return valoracion; }
    public String getRangoPrecio() { return rangoPrecio; }
    public List<Bombo> getMenu() { return menu; }

    public void setMenu(List<Bombo> menu) { this.menu = menu; }
}
