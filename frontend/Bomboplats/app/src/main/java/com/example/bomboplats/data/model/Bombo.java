package com.example.bomboplats.data.model;

import java.util.ArrayList;
import java.util.List;

public class Bombo {
    private String id;
    private String restauranteId;
    private String nombre;
    private String descripcion;
    private String precio;
    private List<String> etiquetas;
    private List<String> fotos;
    private List<String> ingredientes;
    private List<String> alergenos;

    public Bombo(String id, String restauranteId, String nombre, String descripcion, String precio, List<String> etiquetas, List<String> fotos, List<String> ingredientes, List<String> alergenos) {
        this.id = id;
        this.restauranteId = restauranteId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.etiquetas = etiquetas != null ? etiquetas : new ArrayList<>();
        this.fotos = fotos != null ? fotos : new ArrayList<>();
        this.ingredientes = ingredientes != null ? ingredientes : new ArrayList<>();
        this.alergenos = alergenos != null ? alergenos : new ArrayList<>();
    }

    public String getId() { return id; }
    public String getRestauranteId() { return restauranteId; }
    public void setRestauranteId(String restauranteId) { this.restauranteId = restauranteId; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getPrecio() { return precio; }
    public List<String> getEtiquetas() { return etiquetas; }
    public List<String> getFotos() { return fotos; }
    public List<String> getIngredientes() { return ingredientes; }
    public List<String> getAlergenos() { return alergenos; }
}
