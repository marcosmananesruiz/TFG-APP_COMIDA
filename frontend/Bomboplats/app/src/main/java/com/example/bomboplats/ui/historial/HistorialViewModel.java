package com.example.bomboplats.ui.historial;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.BomboConCantidad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistorialViewModel extends AndroidViewModel {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private static final String PREFIX_HISTORIAL = "historial_";
    
    private final MutableLiveData<List<Pedido>> pedidos = new MutableLiveData<>(new ArrayList<>());
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public HistorialViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        cargarHistorial();
    }

    public LiveData<List<Pedido>> getPedidos() {
        return pedidos;
    }

    public void agregarPedido(Pedido pedido) {
        List<Pedido> listaActual = pedidos.getValue();
        if (listaActual == null) listaActual = new ArrayList<>();
        listaActual.add(0, pedido); // Agregar al principio (más reciente primero)
        pedidos.setValue(new ArrayList<>(listaActual));
        guardarHistorial();
    }

    private void guardarHistorial() {
        String currentEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, "usuario1@test.com");
        String json = gson.toJson(pedidos.getValue());
        sharedPreferences.edit().putString(PREFIX_HISTORIAL + currentEmail, json).apply();
    }

    private void cargarHistorial() {
        String currentEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, "usuario1@test.com");
        String json = sharedPreferences.getString(PREFIX_HISTORIAL + currentEmail, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Pedido>>() {}.getType();
            List<Pedido> listaCargada = gson.fromJson(json, type);
            pedidos.setValue(listaCargada != null ? listaCargada : new ArrayList<>());
        } else {
            pedidos.setValue(new ArrayList<>());
        }
    }
}
