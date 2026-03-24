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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewModel extends AndroidViewModel implements FavoritosProvider {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";

    private final SharedPreferences sharedPreferences;
    private final LoginRepository loginRepository;
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> photoUri = new MutableLiveData<>();
    
    private final MutableLiveData<List<String>> favoritos = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Map<String, Integer>> carrito = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<List<String>> addresses = new MutableLiveData<>(new ArrayList<>());

    public UserViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loginRepository = LoginRepository.getInstance(new LoginDataSource(application));

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
        this.addresses.setValue(new ArrayList<>(user.getAddresses()));
        
        sharedPreferences.edit().putString(KEY_CURRENT_USER_EMAIL, user.getEmail()).apply();
        
        if (user.getPhotoPath() != null) {
            photoUri.setValue(user.getPhotoPath());
        } else {
            File photoFile = loginRepository.getUserPhotoFile();
            if (photoFile != null && photoFile.exists()) {
                photoUri.setValue(photoFile.getAbsolutePath());
            } else {
                photoUri.setValue(null);
            }
        }
        
        List<String> listaFavs = new ArrayList<>();
        Map<String, List<String>> favMap = user.getFavoritePlates();
        for (Map.Entry<String, List<String>> entry : favMap.entrySet()) {
            for (String bomboId : entry.getValue()) {
                listaFavs.add(entry.getKey() + ":" + bomboId);
            }
        }
        favoritos.setValue(listaFavs);

        Map<String, Integer> mapaCarritoUI = new HashMap<>();
        Map<String, List<String>> cartMap = user.getCartPlates();
        for (Map.Entry<String, List<String>> entry : cartMap.entrySet()) {
            for (String bomboId : entry.getValue()) {
                String key = entry.getKey() + ":" + bomboId;
                mapaCarritoUI.put(key, mapaCarritoUI.getOrDefault(key, 0) + 1);
            }
        }
        carrito.setValue(mapaCarritoUI);
    }

    public LiveData<String> getName() { return name; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getPassword() { return password; }
    public LiveData<String> getPhotoUri() { return photoUri; }
    public LiveData<List<String>> getFavoritos() { return favoritos; }
    public LiveData<Map<String, Integer>> getCarrito() { return carrito; }
    public LiveData<List<String>> getAddresses() { return addresses; }

    public void addAddress(String address) {
        List<String> current = addresses.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(address);
        addresses.setValue(new ArrayList<>(current));
        saveAddressesToUser(current);
    }

    public void removeAddress(int index) {
        List<String> current = addresses.getValue();
        if (current != null && index >= 0 && index < current.size()) {
            current.remove(index);
            addresses.setValue(new ArrayList<>(current));
            saveAddressesToUser(current);
        }
    }

    private void saveAddressesToUser(List<String> list) {
        LoggedInUser user = loginRepository.getUser();
        if (user != null) {
            user.setAddresses(new ArrayList<>(list));
            loginRepository.saveUser();
        }
    }

    public void setName(String newName) {
        Result<LoggedInUser> result = loginRepository.updateName(newName);
        if (result instanceof Result.Success) name.setValue(newName);
    }

    public void setEmail(String newEmail) {
        Result<LoggedInUser> result = loginRepository.updateEmail(newEmail);
        if (result instanceof Result.Success) {
            this.email.setValue(newEmail);
            sharedPreferences.edit().putString(KEY_CURRENT_USER_EMAIL, newEmail).apply();
            File photoFile = loginRepository.getUserPhotoFile();
            if (photoFile != null && photoFile.exists()) {
                photoUri.setValue(photoFile.getAbsolutePath());
            }
        }
    }

    public void setPassword(String oldPassword, String newPassword) {
        Result<LoggedInUser> result = loginRepository.updatePassword(oldPassword, newPassword);
        if (result instanceof Result.Success) password.setValue(newPassword);
    }

    public void setPhotoUri(String uri) {
        photoUri.setValue(uri);
        LoggedInUser user = loginRepository.getUser();
        if (user != null) {
            user.setPhotoPath(uri);
            loginRepository.saveUser();
        }
    }

    public File getUserPhotoFile() { return loginRepository.getUserPhotoFile(); }

    public void toggleFavorito(String restauranteId, String bomboId) {
        List<String> listaPlana = favoritos.getValue();
        if (listaPlana == null) listaPlana = new ArrayList<>();
        
        String key = restauranteId + ":" + bomboId;
        if (listaPlana.contains(key)) {
            listaPlana.remove(key);
        } else {
            listaPlana.add(key);
        }
        
        favoritos.setValue(new ArrayList<>(listaPlana));
        
        Map<String, List<String>> mapParaJSON = new HashMap<>();
        for (String item : listaPlana) {
            String[] parts = item.split(":");
            if (parts.length == 2) {
                mapParaJSON.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
            }
        }
        loginRepository.setFavoritesMap(mapParaJSON);
    }

    public void setCarritoUI(Map<String, Integer> nuevoCarritoPlano) {
        carrito.setValue(new HashMap<>(nuevoCarritoPlano));
        
        Map<String, List<String>> mapParaJSON = new HashMap<>();
        for (Map.Entry<String, Integer> entry : nuevoCarritoPlano.entrySet()) {
            String[] parts = entry.getKey().split(":");
            if (parts.length == 2) {
                for (int i = 0; i < entry.getValue(); i++) {
                    mapParaJSON.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
                }
            }
        }
        loginRepository.setCartMap(mapParaJSON);
    }

    @Override
    public boolean esFavorito(String restauranteId, String bomboId) {
        List<String> lista = favoritos.getValue();
        return lista != null && lista.contains(restauranteId + ":" + bomboId);
    }
}
