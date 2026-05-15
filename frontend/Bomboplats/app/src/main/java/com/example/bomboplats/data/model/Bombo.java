package com.example.bomboplats.data.model;

import androidx.annotation.Nullable;

import com.example.bomboplats.api.Plato;
import com.example.bomboplats.api.PlatoControllerApi;
import com.example.bomboplats.api.RestauranteControllerApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

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

    // Builder
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

    public Bombo(Plato plato) {
        this.id = plato.getId();
        this.restauranteId = "";
        this.nombre = plato.getNombre();
        this.descripcion = plato.getDescription();
        this.precio = String.valueOf(plato.getPrecio());
        this.etiquetas = plato.getTags();
        this.fotos = new ArrayList<String>(){{ add(plato.getIconUrl()); }};
        this.ingredientes = plato.getPossibleModifications();
        this.alergenos = new ArrayList<>();
    }

    // Getters y setters
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

    // Método equals
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bombo bombo)) return false;
        return this.id.equals(bombo.id);
    }

    // Metodo comprobar hashcode
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
