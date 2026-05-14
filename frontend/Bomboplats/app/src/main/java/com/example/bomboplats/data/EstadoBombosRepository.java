package com.example.bomboplats.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.data.model.EstadoPedido;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio que gestiona el estado de los pedidos activos de forma local.
 * NO descarga información de la base de datos para simular el proceso de entrega y tiempos.
 */
public class EstadoBombosRepository {
    private static EstadoBombosRepository instance;
    private final MutableLiveData<List<EstadoPedido>> pedidosEnEstado = new MutableLiveData<>(new ArrayList<>());

    private List<EstadoPedido> estadoPedidos = new ArrayList<>();
    private static final String PREF_NAME = "bomboplats_estados_prefs";
    private static final String USER_PREFS = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private final Gson gson = new Gson();

    private EstadoBombosRepository() {}

    public static synchronized EstadoBombosRepository getInstance() {
        if (instance == null) {
            instance = new EstadoBombosRepository();
        }
        return instance;
    }

    public LiveData<List<EstadoPedido>> getPedidosEnEstado() {
        return pedidosEnEstado;
    }

    private String getActiveEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CURRENT_USER_EMAIL, null);
    }

    public void cargarDesdeDisco(Context context) {
        String email = getActiveEmail(context);
        if (email == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString("estados_" + email, null);

        List<EstadoPedido> lista = new ArrayList<>();
        if (json != null) {
            try {
                Type type = new TypeToken<ArrayList<EstadoPedido>>() {}.getType();
                lista = gson.fromJson(json, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.estadoPedidos = lista;;
    }

    public void guardarEnDisco(Context context, List<EstadoPedido> lista) {
        String email = getActiveEmail(context);
        if (email == null) return;

        pedidosEnEstado.postValue(new ArrayList<>(lista));
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("estados_" + email, gson.toJson(lista)).apply();
    }

    public void agregarPedido(Context context, EstadoPedido nuevo) {
        cargarDesdeDisco(context);
        List<EstadoPedido> actual = new ArrayList<>(getListaActual());
        actual.add(0, nuevo);
        guardarEnDisco(context, actual);
    }

    public List<EstadoPedido> getListaActual() {
        List<EstadoPedido> actual = this.estadoPedidos;
        if (actual == null) {
            Log.e("", "benja,min netanyahu deja nuestro proyecto porfgavor");
        } else {
            Log.w("", actual.toString());
        }
        return actual != null ? actual : new ArrayList<>();
    }
}
