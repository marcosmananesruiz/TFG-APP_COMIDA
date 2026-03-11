package com.example.bomboplats.ui.cuenta;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.ui.general.FavoritosProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserViewModel extends AndroidViewModel implements FavoritosProvider {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    
    // Prefijo para las claves de cada usuario: "user_email_..."
    private static final String PREFIX_NAME = "name_";
    private static final String PREFIX_PASSWORD = "password_";
    private static final String PREFIX_PHOTO = "photo_";
    private static final String PREFIX_FAVORITOS = "favoritos_";
    private static final String PREFIX_CARRITO = "carrito_";

    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> photoUri = new MutableLiveData<>();
    private final MutableLiveData<List<String>> favoritos = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Map<String, Integer>> carrito = new MutableLiveData<>(new HashMap<>());

    public UserViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Crear cuentas de ejemplo si no existen
        crearCuentasEjemplo();

        // Cargar el último usuario logueado o uno por defecto
        String currentEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, "usuario1@test.com");
        loadUserData(currentEmail);
    }

    private void crearCuentasEjemplo() {
        if (!sharedPreferences.contains(PREFIX_NAME + "usuario1@test.com")) {
            sharedPreferences.edit()
                .putString(PREFIX_NAME + "usuario1@test.com", "Juan Pérez")
                .putString(PREFIX_PASSWORD + "usuario1@test.com", "juan123")
                .apply();
        }
        if (!sharedPreferences.contains(PREFIX_NAME + "usuario2@test.com")) {
            sharedPreferences.edit()
                .putString(PREFIX_NAME + "usuario2@test.com", "María García")
                .putString(PREFIX_PASSWORD + "usuario2@test.com", "maria456")
                .apply();
        }
    }

    public void loadUserData(String userEmail) {
        this.email.setValue(userEmail);
        sharedPreferences.edit().putString(KEY_CURRENT_USER_EMAIL, userEmail).apply();
        
        name.setValue(sharedPreferences.getString(PREFIX_NAME + userEmail, "Usuario"));
        password.setValue(sharedPreferences.getString(PREFIX_PASSWORD + userEmail, "123456"));
        photoUri.setValue(sharedPreferences.getString(PREFIX_PHOTO + userEmail, null));
        
        Set<String> setFavs = sharedPreferences.getStringSet(PREFIX_FAVORITOS + userEmail, new HashSet<>());
        favoritos.setValue(new ArrayList<>(setFavs));

        Set<String> setCarrito = sharedPreferences.getStringSet(PREFIX_CARRITO + userEmail, new HashSet<>());
        Map<String, Integer> mapaCarrito = new HashMap<>();
        for (String item : setCarrito) {
            String[] parts = item.split(":");
            if (parts.length == 2) {
                try {
                    mapaCarrito.put(parts[0], Integer.parseInt(parts[1]));
                } catch (NumberFormatException ignored) {}
            }
        }
        carrito.setValue(mapaCarrito);
    }

    public LiveData<String> getName() { return name; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getPassword() { return password; }
    public LiveData<String> getPhotoUri() { return photoUri; }
    public LiveData<List<String>> getFavoritos() { return favoritos; }
    public LiveData<Map<String, Integer>> getCarrito() { return carrito; }

    public void setName(String newName) {
        String currentEmail = email.getValue();
        name.setValue(newName);
        sharedPreferences.edit().putString(PREFIX_NAME + currentEmail, newName).apply();
    }

    public void setEmail(String newEmail) {
        String currentEmail = email.getValue();
        this.email.setValue(newEmail);
        sharedPreferences.edit().putString(KEY_CURRENT_USER_EMAIL, newEmail).apply();
    }

    public void setPassword(String newPassword) {
        String currentEmail = email.getValue();
        password.setValue(newPassword);
        sharedPreferences.edit().putString(PREFIX_PASSWORD + currentEmail, newPassword).apply();
    }

    public void setPhotoUri(String uri) {
        String currentEmail = email.getValue();
        photoUri.setValue(uri);
        sharedPreferences.edit().putString(PREFIX_PHOTO + currentEmail, uri).apply();
    }
    // Función para agregar o quitar bombos de favoritos.
    public void toggleFavorito(String itemId) {
        // Primero carga la el email y la lista de favoritos. Si no existe la lista, crea una.
        String currentEmail = email.getValue();
        List<String> lista = favoritos.getValue();
        if (lista == null) lista = new ArrayList<>();
        
        if (lista.contains(itemId)) {  // Verifica que al pulsar no haya ningún bombo con la misma ID
            lista.remove(itemId); // Si la hay, elimina el bombo de la lista
        } else {
            lista.add(itemId); // Si no, lo agrega.
        }
        
        favoritos.setValue(new ArrayList<>(lista)); // Carga la lista temportal a la lista normal de favoritos.
        sharedPreferences.edit().putStringSet(PREFIX_FAVORITOS + currentEmail, new HashSet<>(lista)).apply();
    }

    public void setCarrito(Map<String, Integer> nuevoCarrito) {
        String currentEmail = email.getValue();
        carrito.setValue(new HashMap<>(nuevoCarrito));
        
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, Integer> entry : nuevoCarrito.entrySet()) {
            set.add(entry.getKey() + ":" + entry.getValue());
        }
        sharedPreferences.edit().putStringSet(PREFIX_CARRITO + currentEmail, set).apply();
    }

    @Override
    public boolean esFavorito(String itemId) {
        List<String> lista = favoritos.getValue();
        return lista != null && lista.contains(itemId);
    }
}
