package com.example.bomboplats.data.model;

import java.util.ArrayList;
import java.util.List;

public class Bombo {
    private String id;
    private String restauranteId;
    private String nombre;
    private String descripcion;
    private String precio;
    private List<String> ingredientes;
    private List<String> alergenos;
    private List<Integer> fotos; // IDs de recursos para el carrusel

    public Bombo(String id, String restauranteId, String nombre, String descripcion, String precio) {
        this(id, restauranteId, nombre, descripcion, precio, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public Bombo(String id, String restauranteId, String nombre, String descripcion, String precio, List<String> ingredientes, List<String> alergenos, List<Integer> fotos) {
        this.id = id;
        this.restauranteId = restauranteId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.ingredientes = ingredientes;
        this.alergenos = alergenos;
        this.fotos = fotos;
    }

    // Getters
    public String getId() { return id; }
    public String getRestauranteId() { return restauranteId; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getPrecio() { return precio; }
    public List<String> getIngredientes() { return ingredientes; }
    public List<String> getAlergenos() { return alergenos; }
    public List<Integer> getFotos() { return fotos; }

    public void setIngredientes(List<String> ingredientes) { this.ingredientes = ingredientes; }
    public void setAlergenos(List<String> alergenos) { this.alergenos = alergenos; }
    public void setFotos(List<Integer> fotos) { this.fotos = fotos; }
}
