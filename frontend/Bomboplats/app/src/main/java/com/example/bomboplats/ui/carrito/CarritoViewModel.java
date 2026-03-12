package com.example.bomboplats.ui.carrito;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.LoginRepository;
import com.example.bomboplats.data.model.LoggedInUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarritoViewModel extends AndroidViewModel {
    
    private final MutableLiveData<Map<String, Integer>> itemsCarrito = new MutableLiveData<>(new HashMap<>());
    private final LoginRepository loginRepository;
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";

    public CarritoViewModel(@NonNull Application application) {
        super(application);
        loginRepository = LoginRepository.getInstance(new LoginDataSource(application));
        
        // Intentar restaurar sesión si no hay usuario en el repositorio
        LoggedInUser user = loginRepository.getUser();
        if (user == null) {
            SharedPreferences sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, null);
            if (savedEmail != null) {
                loginRepository.loadUserSession(savedEmail);
            }
        }
        
        cargarDatosDesdeJSON();
    }

    public LiveData<Map<String, Integer>> getItemsCarrito() {
        return itemsCarrito;
    }

    private void cargarDatosDesdeJSON() {
        LoggedInUser user = loginRepository.getUser();
        if (user != null) {
            Map<String, Integer> mapaCargado = new HashMap<>();
            List<String> cartIds = user.getCartPlateIds();
            if (cartIds != null) {
                for (String id : cartIds) {
                    mapaCargado.put(id, mapaCargado.getOrDefault(id, 0) + 1);
                }
            }
            itemsCarrito.setValue(mapaCargado);
        }
    }

    public void agregarAlCarrito(String bomboId, int cantidad) {
        Map<String, Integer> mapaActual = itemsCarrito.getValue();
        if (mapaActual != null) {
            int cantidadExistente = mapaActual.getOrDefault(bomboId, 0);
            mapaActual.put(bomboId, cantidadExistente + cantidad);
            itemsCarrito.setValue(new HashMap<>(mapaActual));
            sincronizarConJSON();
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
            sincronizarConJSON();
        }
    }

    public void limpiarCarrito() {
        itemsCarrito.setValue(new HashMap<>());
        sincronizarConJSON();
    }

    private void sincronizarConJSON() {
        Map<String, Integer> mapa = itemsCarrito.getValue();
        if (mapa != null) {
            List<String> listaIds = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : mapa.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    listaIds.add(entry.getKey());
                }
            }
            loginRepository.setCart(listaIds);
        }
    }
}
