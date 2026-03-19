package com.example.bomboplats.data;

import android.content.Context;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.Restaurante;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FoodRepository {
    private static FoodRepository instance;
    private List<Restaurante> restaurantes = new ArrayList<>();
    private List<Bombo> allBombos = new ArrayList<>();
    private final File bombosFile;
    private final Gson gson = new Gson();

    private FoodRepository(Context context) {
        File root = new File(context.getFilesDir(), "documentos");
        File bombosDir = new File(root, "bombos");
        if (!bombosDir.exists()) bombosDir.mkdirs();
        this.bombosFile = new File(bombosDir, "food_data.json");
        
        initializeData(context);
        loadData();
    }

    public static synchronized FoodRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FoodRepository(context.getApplicationContext());
        }
        return instance;
    }

    private void initializeData(Context context) {
        if (!bombosFile.exists()) {
            try (InputStream is = context.getAssets().open("food_data.json");
                 FileOutputStream fos = new FileOutputStream(bombosFile)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadData() {
        try (FileReader reader = new FileReader(bombosFile)) {
            JsonObject rootObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray restaurantsArray = rootObject.getAsJsonArray("restaurantes");
            
            Type listType = new TypeToken<List<Restaurante>>() {}.getType();
            restaurantes = gson.fromJson(restaurantsArray, listType);
            
            allBombos.clear();
            if (restaurantes != null) {
                for (Restaurante r : restaurantes) {
                    if (r.getMenu() != null) {
                        for (Bombo b : r.getMenu()) {
                            b.setRestauranteId(r.getId());
                            allBombos.add(b);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try (FileWriter writer = new FileWriter(bombosFile)) {
            JsonObject root = new JsonObject();
            root.add("restaurantes", gson.toJsonTree(restaurantes));
            gson.toJson(root, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Restaurante> getRestaurantes() {
        return restaurantes;
    }

    public List<Bombo> getBombos() {
        return allBombos;
    }

    public List<Bombo> getBombosPorRestaurante(String restauranteId) {
        for (Restaurante r : restaurantes) {
            if (r.getId().equals(restauranteId)) {
                return r.getMenu();
            }
        }
        return new ArrayList<>();
    }

    public Bombo getBomboPorId(String id) {
        if (id == null) return null;
        
        // Soporte para IDs compuestos "restauranteId:bomboId"
        String restauranteId = null;
        String actualBomboId = id;
        if (id.contains(":")) {
            String[] parts = id.split(":");
            restauranteId = parts[0];
            actualBomboId = parts[1];
        }

        for (Bombo b : allBombos) {
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
}
