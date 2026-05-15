package com.example.bomboplats.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.ui.historial.Pedido;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio que gestiona el historial de pedidos de forma puramente local.
 * Según los requisitos, esta información se simula y no se descarga de la BBDD.
 */
public class HistorialRepository {
    private static HistorialRepository instance;
    private final MutableLiveData<List<Pedido>> pedidos = new MutableLiveData<>(new ArrayList<>());
    
    private static final String PREF_NAME = "bomboplats_historial_prefs";
    private static final String USER_PREFS = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private final Gson gson = new Gson();

    private HistorialRepository() {}

    public static synchronized HistorialRepository getInstance() {
        if (instance == null) {
            instance = new HistorialRepository();
        }
        return instance;
    }

    public LiveData<List<Pedido>> getPedidos() {
        return pedidos;
    }

    private String getActiveEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CURRENT_USER_EMAIL, null);
    }
    // Metodo para cargar del disco
    public void cargarDesdeDisco(Context context) {
        String email = getActiveEmail(context);
        if (email == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString("historial_" + email, null);
        
        List<Pedido> lista = new ArrayList<>();
        if (json != null) {
            try {
                Type type = new TypeToken<ArrayList<Pedido>>() {}.getType();
                lista = gson.fromJson(json, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pedidos.postValue(lista != null ? lista : new ArrayList<>());
    }
    // Metodo para guardar pedido
    public void guardarPedido(Context context, Pedido pedido) {
        String email = getActiveEmail(context);
        if (email == null) return;

        // Cargamos primero para asegurar que tenemos la lista actualizada
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString("historial_" + email, null);
        List<Pedido> lista = new ArrayList<>();
        if (json != null) {
            try {
                Type type = new TypeToken<ArrayList<Pedido>>() {}.getType();
                lista = gson.fromJson(json, type);
            } catch (Exception e) {}
        }
        
        if (lista == null) lista = new ArrayList<>();
        lista.add(0, pedido); // Añadimos al principio (más reciente primero)
        
        pedidos.postValue(lista);
        prefs.edit().putString("historial_" + email, gson.toJson(lista)).apply();
    }
}
