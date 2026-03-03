package com.example.bomboplats.data.model;

public class Bombo {
    private String id;
    private String restauranteId;
    private String nombre;
    private String descripcion;
    private String precio;

    public Bombo(String id, String restauranteId, String nombre, String descripcion, String precio) {
        this.id = id;
        this.restauranteId = restauranteId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    // Getters
    public String getId() { return id; }
    public String getRestauranteId() { return restauranteId; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getPrecio() { return precio; }
}
