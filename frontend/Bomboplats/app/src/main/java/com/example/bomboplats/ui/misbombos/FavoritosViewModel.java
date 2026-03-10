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

    public void toggleFavorito(String bomboId) {
        List<String> listaActual = idsFavoritos.getValue();
        if (listaActual != null) {
            if (listaActual.contains(bomboId)) {
                listaActual.remove(bomboId);
            } else {
                listaActual.add(bomboId);
            }
            idsFavoritos.setValue(new ArrayList<>(listaActual)); // Notificar observadores
            guardarFavoritos();
        }
    }

    @Override
    public boolean esFavorito(String bomboId) {
        List<String> listaActual = idsFavoritos.getValue();
        return listaActual != null && listaActual.contains(bomboId);
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
