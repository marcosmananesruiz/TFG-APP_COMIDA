package com.example.bomboplats.ui.cuenta;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Direccion;
import com.example.bomboplats.api.DireccionControllerApi;
import com.example.bomboplats.api.User;
import com.example.bomboplats.api.UserControllerApi;
import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.LoginRepository;
import com.example.bomboplats.data.Result;
import com.example.bomboplats.data.model.LoggedInUser;
import com.example.bomboplats.ui.general.FavoritosProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class UserViewModel extends AndroidViewModel implements FavoritosProvider {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";

    private final SharedPreferences sharedPreferences;
    private final LoginRepository loginRepository;
    private final DireccionControllerApi direccionApi;
    private final UserControllerApi userApi;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> photoUri = new MutableLiveData<>();
    
    private final MutableLiveData<List<String>> favoritos = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Map<String, Integer>> carrito = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<List<String>> addresses = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Direccion>> allApiAddresses = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> filteredAddresses = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loginRepository = LoginRepository.getInstance(new LoginDataSource(application));
        direccionApi = new DireccionControllerApi();
        userApi = new UserControllerApi();

        refreshUserData();
    }

    public void refreshUserData() {
        executorService.execute(() -> {
            LoggedInUser user = loginRepository.getUser();
            if (user == null) {
                String savedEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, null);
                if (savedEmail != null) {
                    loginRepository.loadUserSession(savedEmail);
                    user = loginRepository.getUser();
                }
            }

            // Cargar TODAS las direcciones de la base de datos para búsqueda
            try {
                List<Direccion> apiDirs = direccionApi.findAll4();
                allApiAddresses.postValue(apiDirs != null ? apiDirs : new ArrayList<>());
            } catch (ApiException e) {
                Log.e("UserViewModel", "Error loading addresses: " + e.getMessage());
            }

            if (user != null) {
                try {
                    // Refrescar datos del usuario desde la API para tener las direcciones actualizadas
                    User apiUser = userApi.getByEmail(user.getEmail());
                    if (apiUser != null) {
                        List<String> userStringDirs = new ArrayList<>();
                        if (apiUser.getDirecciones() != null) {
                            for (Direccion d : apiUser.getDirecciones()) {
                                userStringDirs.add(formatDireccion(d));
                            }
                        }
                        // Actualizar localmente
                        user.setAddresses(userStringDirs);
                    }
                } catch (ApiException e) {
                    Log.e("UserViewModel", "Error refreshing user addresses: " + e.getMessage());
                }

                final LoggedInUser finalUser = user;
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> loadUserData(finalUser));
            }
        });
    }

    public void searchAddresses(String query) {
        if (query == null || query.isEmpty()) {
            filteredAddresses.setValue(new ArrayList<>());
            return;
        }

        List<Direccion> all = allApiAddresses.getValue();
        if (all == null) return;

        List<String> results = all.stream()
                .filter(d -> formatDireccion(d).toLowerCase().contains(query.toLowerCase()))
                .map(this::formatDireccion)
                .collect(Collectors.toList());
        
        filteredAddresses.setValue(results);
    }

    private String formatDireccion(Direccion d) {
        return d.getCalle() + ", " + d.getPortal() + (d.getPiso() != null && !d.getPiso().isEmpty() ? ", " + d.getPiso() : "") + " (" + d.getPoblacion() + ")";
    }

    public void registerAndAssignAddress(String poblacion, String calle, String cp, int portal, String piso) {
        executorService.execute(() -> {
            try {
                Direccion d = new Direccion();
                d.setPoblacion(poblacion);
                d.setCalle(calle);
                d.setCodigoPostal(cp);
                d.setPortal(portal);
                d.setPiso(piso);

                // 1. Guardar dirección en BD
                Direccion savedDir = direccionApi.registerDireccion(d);
                
                // 2. Asignar al usuario actual
                LoggedInUser loggedUser = loginRepository.getUser();
                if (loggedUser != null && savedDir != null) {
                    User apiUser = userApi.getByEmail(loggedUser.getEmail());
                    Set<Direccion> userDirs = apiUser.getDirecciones();
                    if (userDirs == null) userDirs = new LinkedHashSet<>();
                    userDirs.add(savedDir);
                    apiUser.setDirecciones(userDirs);
                    
                    userApi.updateUser(apiUser);
                }
                
                refreshUserData();
            } catch (ApiException e) {
                error.postValue("Error al guardar la dirección en el servidor");
            }
        });
    }

    public void loadUserData(LoggedInUser user) {
        this.userId.setValue(user.getUserId());
        this.email.setValue(user.getEmail());
        this.name.setValue(user.getDisplayName());
        this.password.setValue(user.getPassword());
        this.addresses.setValue(new ArrayList<>(user.getAddresses()));
        
        sharedPreferences.edit().putString(KEY_CURRENT_USER_EMAIL, user.getEmail()).apply();
        
        if (user.getPhotoPath() != null && user.getPhotoPath().startsWith("http")) {
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

    public LiveData<String> getUserId() { return userId; }
    public LiveData<String> getName() { return name; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getPassword() { return password; }
    public LiveData<String> getPhotoUri() { return photoUri; }
    public LiveData<List<String>> getFavoritos() { return favoritos; }
    public LiveData<Map<String, Integer>> getCarrito() { return carrito; }
    public LiveData<List<String>> getAddresses() { return addresses; }
    public LiveData<List<String>> getFilteredAddresses() { return filteredAddresses; }
    public LiveData<String> getError() { return error; }

    public void removeAddress(int index) {
        refreshUserData();
    }

    public void setName(String newName) {
        executorService.execute(() -> {
            Result<LoggedInUser> result = loginRepository.updateName(newName);
            if (result instanceof Result.Success) name.postValue(newName);
        });
    }

    public Result<LoggedInUser> setEmail(String newEmail) {
        Result<LoggedInUser> result = loginRepository.updateEmail(newEmail);
        if (result instanceof Result.Success) {
            email.postValue(newEmail);
            sharedPreferences.edit().putString(KEY_CURRENT_USER_EMAIL, newEmail).apply();
        }
        return result;
    }

    public Result<LoggedInUser> setPassword(String oldPass, String newPass) {
        Result<LoggedInUser> result = loginRepository.updatePassword(oldPass, newPass);
        if (result instanceof Result.Success) {
            password.postValue(newPass);
        }
        return result;
    }

    public File getUserPhotoFile() {
        return loginRepository.getUserPhotoFile();
    }

    public void setPhotoUri(String uri) {
        photoUri.setValue(uri);
        executorService.execute(() -> {
            LoggedInUser user = loginRepository.getUser();
            if (user != null) {
                user.setPhotoPath(uri);
                loginRepository.saveUser();
            }
        });
    }

    public void toggleFavorito(String restauranteId, String bomboId) {
        List<String> currentList = favoritos.getValue();
        List<String> listaPlana = (currentList == null) ? new ArrayList<>() : new ArrayList<>(currentList);
        
        String key = restauranteId + ":" + bomboId;
        if (listaPlana.contains(key)) {
            listaPlana.remove(key);
        } else {
            listaPlana.add(key);
        }
        
        favoritos.setValue(new ArrayList<>(listaPlana));
        
        final List<String> finalLista = new ArrayList<>(listaPlana);
        executorService.execute(() -> {
            Map<String, List<String>> mapParaAPI = new HashMap<>();
            for (String item : finalLista) {
                String[] parts = item.split(":");
                if (parts.length == 2) {
                    mapParaAPI.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
                }
            }
            loginRepository.setFavoritesMap(mapParaAPI);
        });
    }

    public void setCarritoUI(Map<String, Integer> nuevoCarritoPlano) {
        carrito.setValue(new HashMap<>(nuevoCarritoPlano));
        
        final Map<String, Integer> finalCarrito = new HashMap<>(nuevoCarritoPlano);
        executorService.execute(() -> {
            Map<String, List<String>> mapParaAPI = new HashMap<>();
            for (Map.Entry<String, Integer> entry : finalCarrito.entrySet()) {
                String[] parts = entry.getKey().split(":");
                if (parts.length == 2) {
                    for (int i = 0; i < entry.getValue(); i++) {
                        mapParaAPI.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
                    }
                }
            }
            loginRepository.setCartMap(mapParaAPI);
        });
    }

    @Override
    public boolean esFavorito(String restauranteId, String bomboId) {
        List<String> lista = favoritos.getValue();
        return lista != null && lista.contains(restauranteId + ":" + bomboId);
    }

    public void clearError() { error.setValue(null); }

    public void deleteAccount() {
        executorService.execute(() -> {
            loginRepository.deleteAccount();
            sharedPreferences.edit().remove(KEY_CURRENT_USER_EMAIL).apply();
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
