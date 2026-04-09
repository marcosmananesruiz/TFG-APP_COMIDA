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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarritoViewModel extends AndroidViewModel {
    
    // Clave: "restauranteId:bomboId", Valor: cantidad
    private final MutableLiveData<Map<String, Integer>> itemsCarrito = new MutableLiveData<>(new HashMap<>());
    private final LoginRepository loginRepository;
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CarritoViewModel(@NonNull Application application) {
        super(application);
        loginRepository = LoginRepository.getInstance(new LoginDataSource(application));
        
        // Realizar la carga inicial en un hilo secundario para evitar NetworkOnMainThreadException
        executorService.execute(() -> {
            // Intentar restaurar sesión si no hay usuario en el repositorio
            LoggedInUser user = loginRepository.getUser();
            if (user == null) {
                SharedPreferences sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String savedEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, null);
                if (savedEmail != null) {
                    loginRepository.loadUserSession(savedEmail);
                }
            }
            
            // Cargar datos (que ahora vienen de la API a través del usuario)
            cargarDatosDesdeAPI();
        });
    }

    public LiveData<Map<String, Integer>> getItemsCarrito() {
        return itemsCarrito;
    }

    private void cargarDatosDesdeAPI() {
        LoggedInUser user = loginRepository.getUser();
        if (user != null) {
            Map<String, Integer> mapaCargado = new HashMap<>();
            // Nueva estructura: Map<RestauranteId, List<BomboId>>
            Map<String, List<String>> cartMap = user.getCartPlates();
            if (cartMap != null) {
                for (Map.Entry<String, List<String>> entry : cartMap.entrySet()) {
                    String restauranteId = entry.getKey();
                    for (String bomboId : entry.getValue()) {
                        String key = restauranteId + ":" + bomboId;
                        mapaCargado.put(key, mapaCargado.getOrDefault(key, 0) + 1);
                    }
                }
            }
            itemsCarrito.postValue(mapaCargado);
        }
    }

    public void agregarAlCarrito(String itemKey, int cantidad) {
        Map<String, Integer> mapaActual = itemsCarrito.getValue();
        if (mapaActual == null) mapaActual = new HashMap<>();
        
        int cantidadExistente = mapaActual.getOrDefault(itemKey, 0);
        mapaActual.put(itemKey, cantidadExistente + cantidad);
        itemsCarrito.setValue(new HashMap<>(mapaActual));
        sincronizarConAPI();
    }

    public void removerDelCarrito(String itemKey) {
        Map<String, Integer> mapaActual = itemsCarrito.getValue();
        if (mapaActual != null && mapaActual.containsKey(itemKey)) {
            int cantidadActual = mapaActual.get(itemKey);
            if (cantidadActual > 1) {
                mapaActual.put(itemKey, cantidadActual - 1);
            } else {
                mapaActual.remove(itemKey);
            }
            itemsCarrito.setValue(new HashMap<>(mapaActual));
            sincronizarConAPI();
        }
    }

    public void limpiarCarrito() {
        itemsCarrito.setValue(new HashMap<>());
        sincronizarConAPI();
    }

    private void sincronizarConAPI() {
        final Map<String, Integer> mapaUI = itemsCarrito.getValue();
        if (mapaUI != null) {
            executorService.execute(() -> {
                // Convertir el mapa plano "restauranteId:bomboId" al mapa jerárquico
                Map<String, List<String>> mapParaAPI = new HashMap<>();
                for (Map.Entry<String, Integer> entry : mapaUI.entrySet()) {
                    String[] parts = entry.getKey().split(":");
                    if (parts.length == 2) {
                        String restauranteId = parts[0];
                        String bomboId = parts[1];
                        for (int i = 0; i < entry.getValue(); i++) {
                            mapParaAPI.computeIfAbsent(restauranteId, k -> new ArrayList<>()).add(bomboId);
                        }
                    }
                }
                loginRepository.setCartMap(mapParaAPI);
            });
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
