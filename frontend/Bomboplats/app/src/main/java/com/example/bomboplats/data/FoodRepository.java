package com.example.bomboplats.data;

import android.content.Context;
import android.util.Log;
import com.example.bomboplats.api.ApiException;
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
    private final List<com.example.bomboplats.data.model.Restaurante> cachedRestaurantes = new ArrayList<>();
    private final List<Bombo> cachedBombos = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private FoodRepository(Context context) {
        this.restauranteApi = new RestauranteControllerApi();
        // Carga inicial asíncrona para no bloquear
        executorService.execute(this::refreshDataSync);
    }

    public static synchronized FoodRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FoodRepository(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Refresca los datos desde la API de forma síncrona (debe llamarse desde hilo secundario)
     */
    public void refreshDataSync() {
        try {
            List<Restaurante> apiRestaurantes = restauranteApi.findAll1();
            if (apiRestaurantes != null) {
                synchronized (cachedRestaurantes) {
                    cachedRestaurantes.clear();
                    cachedBombos.clear();
                    for (Restaurante apiR : apiRestaurantes) {
                        com.example.bomboplats.data.model.Restaurante localR = convertToLocalRestaurante(apiR);
                        cachedRestaurantes.add(localR);
                        if (localR.getMenu() != null) {
                            cachedBombos.addAll(localR.getMenu());
                        }
                    }
                }
            }
        } catch (ApiException e) {
            Log.e("FoodRepository", "Error refreshing data: " + e.getMessage());
        }
    }

    public List<com.example.bomboplats.data.model.Restaurante> getRestaurantes() {
        synchronized (cachedRestaurantes) {
            return new ArrayList<>(cachedRestaurantes);
        }
    }

    public List<Bombo> getBombos() {
        synchronized (cachedRestaurantes) {
            return new ArrayList<>(cachedBombos);
        }
    }

    public List<Bombo> getBombosPorRestaurante(String restauranteId) {
        for (com.example.bomboplats.data.model.Restaurante r : getRestaurantes()) {
            if (r.getId().equals(restauranteId)) {
                return r.getMenu();
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
        String iconUrl = (apiR.getIconUrls() != null && !apiR.getIconUrls().isEmpty()) 
                ? apiR.getIconUrls().get(0) : "";

        com.example.bomboplats.data.model.Restaurante localR = new com.example.bomboplats.data.model.Restaurante(
                apiR.getId(),
                apiR.getNombre(),
                apiR.getDescription(),
                iconUrl,
                apiR.getTags() != null ? new ArrayList<>(apiR.getTags()) : new ArrayList<>()
        );

        List<Bombo> menu = new ArrayList<>();
        if (apiR.getPlatos() != null) {
            for (Plato p : apiR.getPlatos()) {
                List<String> fotos = (p.getIconUrl() != null) ? new ArrayList<>(Arrays.asList(p.getIconUrl())) : new ArrayList<>();
                Bombo b = new Bombo(
                        p.getId(),
                        apiR.getId(),
                        p.getNombre(),
                        p.getDescription(),
                        p.getPrecio() != null ? String.valueOf(p.getPrecio()) : "0.0",
                        p.getTags() != null ? new ArrayList<>(p.getTags()) : new ArrayList<>(),
                        fotos,
                        p.getPossibleModifications() != null ? new ArrayList<>(p.getPossibleModifications()) : new ArrayList<>(),
                        new ArrayList<>() // Alergenos no disponibles en Plato API
                );
                menu.add(b);
            }
        }
        localR.setMenu(menu);
        return localR;
    }
}
