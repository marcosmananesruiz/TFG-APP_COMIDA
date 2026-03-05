package com.example.bomboplats.ui.carrito;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CarritoViewModel extends AndroidViewModel {
    private static final String PREFS_NAME = "bomboplats_prefs";
    private static final String KEY_CARRITO = "carrito_items";
    private static final String KEY_FAVORITOS = "favoritos_items";
    
    private final MutableLiveData<Map<String, Integer>> itemsCarrito = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Set<String>> favoritos = new MutableLiveData<>(new HashSet<>());
    private final SharedPreferences sharedPreferences;

    public CarritoViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        cargarDatosPersistentes();
    }

    public LiveData<Map<String, Integer>> getItemsCarrito() {
        return itemsCarrito;
    }

    public LiveData<Set<String>> getFavoritos() {
        return favoritos;
    }

    public void agregarAlCarrito(String bomboId, int cantidad) {
        Map<String, Integer> mapaActual = itemsCarrito.getValue();
        if (mapaActual != null) {
            int cantidadExistente = mapaActual.getOrDefault(bomboId, 0);
            mapaActual.put(bomboId, cantidadExistente + cantidad);
            itemsCarrito.setValue(new HashMap<>(mapaActual));
            guardarCarrito();
        }
    }

    public void removerDelCarrito(String bomboId) {
        Map<String, Integer> mapaActual = itemsCarrito.getValue();
        if (mapaActual != null && mapaActual.containsKey(bomboId)) {
            int cantidadActual = mapaActual.get(bomboId);
            if (cantidadActual > 1) {
                mapaActual.put(bomboId, cantidadActual - 1);
            } else {
                mapaActual.remove(bomboId);
            }
            itemsCarrito.setValue(new HashMap<>(mapaActual));
            guardarCarrito();
        }
    }

    public void limpiarCarrito() {
        itemsCarrito.setValue(new HashMap<>());
        guardarCarrito();
    }

    public boolean esFavorito(String bomboId) {
        Set<String> setFavoritos = favoritos.getValue();
        return setFavoritos != null && setFavoritos.contains(bomboId);
    }

    public void alternarFavorito(String bomboId) {
        Set<String> setFavoritos = favoritos.getValue();
        if (setFavoritos != null) {
            Set<String> nuevoSet = new HashSet<>(setFavoritos);
            if (nuevoSet.contains(bomboId)) {
                nuevoSet.remove(bomboId);
            } else {
                nuevoSet.add(bomboId);
            }
            favoritos.setValue(nuevoSet);
            guardarFavoritos();
        }
    }

    // Persistencia
    private void guardarCarrito() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Map<String, Integer> mapa = itemsCarrito.getValue();
        if (mapa != null) {
            Set<String> set = new HashSet<>();
            for (Map.Entry<String, Integer> entry : mapa.entrySet()) {
                set.add(entry.getKey() + ":" + entry.getValue());
            }
            editor.putStringSet(KEY_CARRITO, set);
            editor.apply();
        }
    }

    private void guardarFavoritos() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = favoritos.getValue();
        if (set != null) {
            editor.putStringSet(KEY_FAVORITOS, new HashSet<>(set));
            editor.apply();
        }
    }

    private void cargarDatosPersistentes() {
        // Cargar Carrito
        Set<String> setCarrito = sharedPreferences.getStringSet(KEY_CARRITO, new HashSet<>());
        Map<String, Integer> mapaCargado = new HashMap<>();
        for (String item : setCarrito) {
            String[] parts = item.split(":");
            if (parts.length == 2) {
                try {
                    mapaCargado.put(parts[0], Integer.parseInt(parts[1]));
                } catch (NumberFormatException ignored) {}
            }
        }
        itemsCarrito.setValue(mapaCargado);

        // Cargar Favoritos
        Set<String> setFavoritos = sharedPreferences.getStringSet(KEY_FAVORITOS, new HashSet<>());
        favoritos.setValue(new HashSet<>(setFavoritos));
    }
}
