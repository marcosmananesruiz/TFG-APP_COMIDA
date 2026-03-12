package com.example.bomboplats.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.data.model.EstadoPedido;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EstadoBombosRepository {
    private static EstadoBombosRepository instance;
    private final MutableLiveData<List<EstadoPedido>> pedidosEnEstado = new MutableLiveData<>(new ArrayList<>());
    private static final String PREF_NAME = "user_prefs";
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

    private File getUsersDir(Context context) {
        File root = new File(context.getFilesDir(), "documentos");
        File usersDir = new File(root, "users");
        if (!usersDir.exists()) usersDir.mkdirs();
        return usersDir;
    }

    private String getActiveEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CURRENT_USER_EMAIL, "usuario1@test.com");
    }

    public void cargarDesdeDisco(Context context) {
        String email = getActiveEmail(context);
        File file = new File(getUsersDir(context), email + "_states.json");
        
        List<EstadoPedido> lista = new ArrayList<>();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<ArrayList<EstadoPedido>>() {}.getType();
                lista = gson.fromJson(reader, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (lista == null) lista = new ArrayList<>();
        pedidosEnEstado.postValue(lista);
    }

    public void guardarEnDisco(Context context, List<EstadoPedido> lista) {
        pedidosEnEstado.postValue(new ArrayList<>(lista));
        String email = getActiveEmail(context);
        File file = new File(getUsersDir(context), email + "_states.json");
        
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(lista, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EstadoPedido> getListaActual() {
        List<EstadoPedido> actual = pedidosEnEstado.getValue();
        return actual != null ? actual : new ArrayList<>();
    }
}
