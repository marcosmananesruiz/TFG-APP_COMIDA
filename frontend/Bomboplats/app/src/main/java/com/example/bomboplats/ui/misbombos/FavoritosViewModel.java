package com.example.bomboplats.ui.misbombos;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.ui.general.FavoritosProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritosViewModel extends AndroidViewModel implements FavoritosProvider {
    private static final String PREFS_NAME = "bomboplats_prefs";
    private static final String KEY_FAVORITOS = "favoritos_items";
    
    // Almacena claves compuestas "restauranteId:bomboId"
    private final MutableLiveData<List<String>> idsFavoritos = new MutableLiveData<>(new ArrayList<>());
    private final SharedPreferences sharedPreferences;

    public FavoritosViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        cargarFavoritos();
    }

    public LiveData<List<String>> getIdsFavoritos() {
        return idsFavoritos;
    }

    public void toggleFavorito(String restauranteId, String bomboId) {
        List<String> listaActual = idsFavoritos.getValue();
        if (listaActual != null) {
            String key = restauranteId + ":" + bomboId;
            if (listaActual.contains(key)) {
                listaActual.remove(key);
            } else {
                listaActual.add(key);
            }
            idsFavoritos.setValue(new ArrayList<>(listaActual)); // Notificar observadores
            guardarFavoritos();
        }
    }

    @Override
    public boolean esFavorito(String restauranteId, String bomboId) {
        List<String> listaActual = idsFavoritos.getValue();
        String key = restauranteId + ":" + bomboId;
        return listaActual != null && listaActual.contains(key);
    }

    private void guardarFavoritos() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>(idsFavoritos.getValue());
        editor.putStringSet(KEY_FAVORITOS, set);
        editor.apply();
    }

    private void cargarFavoritos() {
        Set<String> setFavoritos = sharedPreferences.getStringSet(KEY_FAVORITOS, new HashSet<>());
        idsFavoritos.setValue(new ArrayList<>(setFavoritos));
    }
}
