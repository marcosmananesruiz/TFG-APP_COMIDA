package com.example.bomboplats.ui.carrito;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.Map;

public class CarritoViewModel extends ViewModel {
    private final MutableLiveData<Map<String, Integer>> itemsCarrito = new MutableLiveData<>(new HashMap<>());

    public LiveData<Map<String, Integer>> getItemsCarrito() {
        return itemsCarrito;
    }

    public void agregarAlCarrito(String bomboId, int cantidad) {
        Map<String, Integer> mapaActual = itemsCarrito.getValue();
        if (mapaActual != null) {
            int cantidadExistente = mapaActual.containsKey(bomboId) ? mapaActual.get(bomboId) : 0;
            mapaActual.put(bomboId, cantidadExistente + cantidad);
            itemsCarrito.setValue(mapaActual);
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
            itemsCarrito.setValue(mapaActual);
        }
    }

    public void limpiarCarrito() {
        itemsCarrito.setValue(new HashMap<>());
    }
}
