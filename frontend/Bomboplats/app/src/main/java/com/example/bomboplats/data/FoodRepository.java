package com.example.bomboplats.data;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Direccion;
import com.example.bomboplats.api.Plato;
import com.example.bomboplats.api.Restaurante;
import com.example.bomboplats.api.RestauranteControllerApi;
import com.example.bomboplats.data.model.Bombo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository that manages food data (Restaurants and Dishes/Bombos).
 * Migrated to use REST API.
 */
public class FoodRepository {
    private static FoodRepository instance;
    private final RestauranteControllerApi restauranteApi;
    private final MutableLiveData<List<com.example.bomboplats.data.model.Restaurante>> restaurantesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final List<Bombo> cachedBombos = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private FoodRepository(Context context) {
        this.restauranteApi = new RestauranteControllerApi();
        // Carga inicial asíncrona
        refreshData();
    }

    public static synchronized FoodRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FoodRepository(context.getApplicationContext());
        }
        return instance;
    }

    public void refreshData() {
        executorService.execute(this::refreshDataSync);
    }

    /**
     * Refresca los datos desde la API de forma síncrona (debe llamarse desde hilo secundario)
     */
    private void refreshDataSync() {
        try {
            List<Restaurante> apiRestaurantes = restauranteApi.findAll1();
            if (apiRestaurantes != null) {
                List<com.example.bomboplats.data.model.Restaurante> locales = new ArrayList<>();
                List<Bombo> bombos = new ArrayList<>();
                
                for (Restaurante apiR : apiRestaurantes) {
                    com.example.bomboplats.data.model.Restaurante localR = convertToLocalRestaurante(apiR);
                    locales.add(localR);
                    if (localR.getMenu() != null) {
                        bombos.addAll(localR.getMenu());
                    }
                }
                
                synchronized (cachedBombos) {
                    cachedBombos.clear();
                    cachedBombos.addAll(bombos);
                }
                
                restaurantesLiveData.postValue(locales);
            }
        } catch (ApiException e) {
            Log.e("FoodRepository", "Error refreshing data: " + e.getMessage());
            // No limpiamos la lista para que el usuario pueda seguir viéndolos offline si ya se cargaron
        }
    }

    public LiveData<List<com.example.bomboplats.data.model.Restaurante>> getRestaurantesLiveData() {
        return restaurantesLiveData;
    }

    public List<com.example.bomboplats.data.model.Restaurante> getRestaurantes() {
        return restaurantesLiveData.getValue();
    }

    public List<Bombo> getBombos() {
        synchronized (cachedBombos) {
            return new ArrayList<>(cachedBombos);
        }
    }

    public List<Bombo> getBombosPorRestaurante(String restauranteId) {
        List<com.example.bomboplats.data.model.Restaurante> current = restaurantesLiveData.getValue();
        if (current != null) {
            for (com.example.bomboplats.data.model.Restaurante r : current) {
                if (r.getId().equals(restauranteId)) {
                    return r.getMenu();
                }
            }
        }
        return new ArrayList<>();
    }

    public Bombo getBomboPorId(String id) {
        if (id == null) return null;
        
        String restauranteId = null;
        String actualBomboId = id;
        if (id.contains(":")) {
            String[] parts = id.split(":");
            restauranteId = parts[0];
            actualBomboId = parts[1];
        }

        for (Bombo b : getBombos()) {
            if (restauranteId != null) {
                if (actualBomboId.equals(b.getId()) && restauranteId.equals(b.getRestauranteId())) {
                    return b;
                }
            } else {
                if (actualBomboId.equals(b.getId())) return b;
            }
        }
        return null;
    }

    // --- CONVERSORES ---

    private com.example.bomboplats.data.model.Restaurante convertToLocalRestaurante(Restaurante apiR) {
        List<String> fotos = apiR.getIconUrls() != null ? new ArrayList<>(apiR.getIconUrls()) : new ArrayList<>();
        List<String> etiquetas = apiR.getTags() != null ? new ArrayList<>(apiR.getTags()) : new ArrayList<>();
        
        String ubicacion = "";
        if (apiR.getDirecciones() != null && !apiR.getDirecciones().isEmpty()) {
            Direccion d = apiR.getDirecciones().get(0);
            ubicacion = d.getCalle() + ", " + d.getPortal() + " (" + d.getPoblacion() + ")";
        }

        float valoracion = apiR.getRating() != null ? apiR.getRating().floatValue() : 0.0f;

        com.example.bomboplats.data.model.Restaurante localR = new com.example.bomboplats.data.model.Restaurante(
                apiR.getId(),
                apiR.getNombre(),
                apiR.getDescription(),
                etiquetas,
                fotos,
                ubicacion,
                valoracion,
                "€€", // Rango de precio por defecto si no viene en API
                new ArrayList<>()
        );

        List<Bombo> menu = new ArrayList<>();
        if (apiR.getPlatos() != null) {
            for (Plato p : apiR.getPlatos()) {
                List<String> fotosPlato = (p.getIconUrl() != null) ? new ArrayList<>(Arrays.asList(p.getIconUrl())) : new ArrayList<>();
                Bombo b = new Bombo(
                        p.getId(),
                        apiR.getId(),
                        p.getNombre(),
                        p.getDescription(),
                        p.getPrecio() != null ? String.valueOf(p.getPrecio()) : "0.0",
                        p.getTags() != null ? new ArrayList<>(p.getTags()) : new ArrayList<>(),
                        fotosPlato,
                        p.getPossibleModifications() != null ? new ArrayList<>(p.getPossibleModifications()) : new ArrayList<>(),
                        new ArrayList<>()
                );
                menu.add(b);
            }
        }
        localR.setMenu(menu);
        return localR;
    }
}
