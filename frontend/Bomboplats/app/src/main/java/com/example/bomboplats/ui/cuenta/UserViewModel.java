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
import java.util.List;
import java.util.Map;

public class UserViewModel extends AndroidViewModel implements FavoritosProvider {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private static final String PREFIX_PHOTO = "photo_";

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

        // Intentar restaurar sesión si no hay usuario
        LoggedInUser user = loginRepository.getUser();
        if (user == null) {
            String savedEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, null);
            if (savedEmail != null) {
                loginRepository.loadUserSession(savedEmail);
                user = loginRepository.getUser();
            }
        }

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
        
        favoritos.setValue(new ArrayList<>(user.getFavoritePlateIds()));

        Map<String, Integer> mapaCarrito = new HashMap<>();
        if (user.getCartPlateIds() != null) {
            for (String id : user.getCartPlateIds()) {
                mapaCarrito.put(id, mapaCarrito.getOrDefault(id, 0) + 1);
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
        if (currentEmail != null) {
            photoUri.setValue(uri);
            sharedPreferences.edit().putString(PREFIX_PHOTO + currentEmail, uri).apply();
        }
    }

    public void toggleFavorito(String itemId) {
        List<String> lista = favoritos.getValue();
        if (lista == null) lista = new ArrayList<>();
        
        if (lista.contains(itemId)) {
            lista.remove(itemId);
        } else {
            lista.add(itemId);
        }
        
        favoritos.setValue(new ArrayList<>(lista));
        loginRepository.setFavorites(lista);
    }

    public void setCarrito(Map<String, Integer> nuevoCarrito) {
        carrito.setValue(new HashMap<>(nuevoCarrito));
        List<String> listaIds = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : nuevoCarrito.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                listaIds.add(entry.getKey());
            }
        }
        loginRepository.setCart(listaIds);
    }

    @Override
    public boolean esFavorito(String itemId) {
        List<String> lista = favoritos.getValue();
        return lista != null && lista.contains(itemId);
    }
}
