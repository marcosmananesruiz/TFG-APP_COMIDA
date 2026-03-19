package com.example.bomboplats.ui.historial;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistorialViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Pedido>> pedidos = new MutableLiveData<>(new ArrayList<>());
    private final File usersDir;
    private final Gson gson = new Gson();
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";

    public HistorialViewModel(@NonNull Application application) {
        super(application);
        // Usamos siempre "documentos" para coincidir con el resto de la app
        File root = new File(application.getFilesDir(), "documentos");
        this.usersDir = new File(root, "users");
        if (!usersDir.exists()) usersDir.mkdirs();
        cargarHistorial();
    }

    public LiveData<List<Pedido>> getPedidos() {
        return pedidos;
    }

    public void agregarPedido(Pedido pedido) {
        List<Pedido> listaActual = pedidos.getValue();
        if (listaActual == null) listaActual = new ArrayList<>();
        listaActual.add(0, pedido);
        pedidos.setValue(new ArrayList<>(listaActual));
        guardarHistorial();
    }

    private void guardarHistorial() {
        String email = getActiveEmail();
        if (email == null) return;
        
        File file = new File(usersDir, email + "_history.json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(pedidos.getValue(), writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarHistorial() {
        String email = getActiveEmail();
        if (email == null) return;

        File file = new File(usersDir, email + "_history.json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<ArrayList<Pedido>>() {}.getType();
                List<Pedido> listaCargada = gson.fromJson(reader, type);
                pedidos.setValue(listaCargada != null ? listaCargada : new ArrayList<>());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getActiveEmail() {
        SharedPreferences prefs = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CURRENT_USER_EMAIL, null);
    }
}
