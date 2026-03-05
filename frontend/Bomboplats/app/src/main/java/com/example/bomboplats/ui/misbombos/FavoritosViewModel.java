package com.example.bomboplats.ui.misbombos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class FavoritosViewModel extends ViewModel {
    private final MutableLiveData<List<String>> idsFavoritos = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<String>> getIdsFavoritos() {
        return idsFavoritos;
    }

    public void toggleFavorito(String bomboId) {
        List<String> listaActual = idsFavoritos.getValue();
        if (listaActual != null) {
            if (listaActual.contains(bomboId)) {
                listaActual.remove(bomboId);
            } else {
                listaActual.add(bomboId);
            }
            idsFavoritos.setValue(listaActual);
        }
    }

    public boolean esFavorito(String bomboId) {
        List<String> listaActual = idsFavoritos.getValue();
        return listaActual != null && listaActual.contains(bomboId);
    }
}
