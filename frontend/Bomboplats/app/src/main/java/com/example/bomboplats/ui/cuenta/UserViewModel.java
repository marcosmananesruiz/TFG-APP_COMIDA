package com.example.bomboplats.ui.cuenta;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.LoginRepository;
import com.example.bomboplats.data.Result;
import com.example.bomboplats.data.model.LoggedInUser;
import com.example.bomboplats.ui.general.FavoritosProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserViewModel extends AndroidViewModel implements FavoritosProvider {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    
    private static final String PREFIX_PHOTO = "photo_";
    private static final String PREFIX_FAVORITOS = "favoritos_";
    private static final String PREFIX_CARRITO = "carrito_";

    private final SharedPreferences sharedPreferences;
    private final LoginRepository loginRepository;
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> photoUri = new MutableLiveData<>();
    private final MutableLiveData<List<String>> favoritos = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Map<String, Integer>> carrito = new MutableLiveData<>(new HashMap<>());

    public UserViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loginRepository = LoginRepository.getInstance(new LoginDataSource(application));

        LoggedInUser user = loginRepository.getUser();
        if (user != null) {
            loadUserData(user);
        }
    }

    public void loadUserData(LoggedInUser user) {
        this.email.setValue(user.getEmail());
        this.name.setValue(user.getDisplayName());
        this.password.setValue(user.getPassword());
        
        String userEmail = user.getEmail();
        sharedPreferences.edit().putString(KEY_CURRENT_USER_EMAIL, userEmail).apply();
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
        Result<LoggedInUser> result = loginRepository.updateName(newName);
        if (result instanceof Result.Success) {
            name.setValue(newName);
        }
    }

    public void setEmail(String newEmail) {
        Result<LoggedInUser> result = loginRepository.updateEmail(newEmail);
        if (result instanceof Result.Success) {
            this.email.setValue(newEmail);
            sharedPreferences.edit().putString(KEY_CURRENT_USER_EMAIL, newEmail).apply();
        }
    }

    public void setPassword(String oldPassword, String newPassword) {
        Result<LoggedInUser> result = loginRepository.updatePassword(oldPassword, newPassword);
        if (result instanceof Result.Success) {
            password.setValue(newPassword);
        }
    }

    public void setPhotoUri(String uri) {
        String currentEmail = email.getValue();
        photoUri.setValue(uri);
        sharedPreferences.edit().putString(PREFIX_PHOTO + currentEmail, uri).apply();
    }

    public void toggleFavorito(String itemId) {
        String currentEmail = email.getValue();
        List<String> lista = favoritos.getValue();
        if (lista == null) lista = new ArrayList<>();
        
        if (lista.contains(itemId)) {
            lista.remove(itemId);
        } else {
            lista.add(itemId);
        }
        
        favoritos.setValue(new ArrayList<>(lista));
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
