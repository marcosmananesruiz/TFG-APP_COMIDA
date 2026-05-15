package com.example.bomboplats.ui.cuenta;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
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
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.LoggedInUser;
import com.example.bomboplats.data.model.StagedBombo;
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
    
    private final MutableLiveData<List<Bombo>> favoritos = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<StagedBombo>> carrito = new MutableLiveData<>(new ArrayList<>());
    
    private final MutableLiveData<List<Direccion>> userAddressesObjects = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> addresses = new MutableLiveData<>(new ArrayList<>());
    
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Result<Boolean>> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> accountDeleted = new MutableLiveData<>(false);

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

            if (user != null) {
                // Notificamos datos locales inmediatamente para que no aparezca el "default"
                final LoggedInUser initialUser = user;
                new Handler(Looper.getMainLooper()).post(() -> loadUserData(initialUser));

                try {
                    User apiUser = userApi.getByEmail(user.getEmail());
                    if (apiUser != null) {
                        List<Direccion> dirs = new ArrayList<>();
                        List<String> userStringDirs = new ArrayList<>();
                        if (apiUser.getDirecciones() != null) {
                            for (Direccion d : apiUser.getDirecciones()) {
                                dirs.add(d);
                                userStringDirs.add(formatDireccion(d));
                            }
                        }
                        userAddressesObjects.postValue(dirs);
                        user.setAddresses(userStringDirs);
                        user.setUserId(apiUser.getId());
                        userId.postValue(apiUser.getId());
                        
                        // Actualizamos nombre y foto desde API si han cambiado
                        user.setDisplayName(apiUser.getNickname());
                        user.setPhotoPath(apiUser.getIconUrl());
                        
                        loginRepository.saveUser(); // Esto guarda la copia offline actualizada
                    }
                } catch (ApiException e) {
                    Log.e("UserViewModel", "Error refreshing user data (Offline mode?): " + e.getMessage());
                }

                final LoggedInUser finalUser = user;
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> loadUserData(finalUser));
            }
        });
    }

    public String formatDireccion(Direccion d) {
        return d.getCalle() + ", " + d.getPortal() + (d.getPiso() != null && !d.getPiso().isEmpty() ? ", " + d.getPiso() : "") + " (" + d.getPoblacion() + ")";
    }

    public void addAddressToUser(Direccion direccion) {
        executorService.execute(() -> {
            try {
                LoggedInUser loggedUser = loginRepository.getUser();
                if (loggedUser != null) {
                    User apiUser = userApi.getByEmail(loggedUser.getEmail());
                    if (apiUser == null || apiUser.getId() == null) return;
                    
                    Set<Direccion> userDirs = apiUser.getDirecciones();
                    if (userDirs == null) userDirs = new LinkedHashSet<>();
                    
                    boolean exists = false;
                    for (Direccion d : userDirs) {
                        if (d.getId().equals(direccion.getId())) {
                            exists = true;
                            break;
                        }
                    }
                    
                    if (!exists) {
                        userDirs.add(direccion);
                        apiUser.setDirecciones(userDirs);
                        userApi.updateUser(apiUser);
                        refreshUserData();
                    }
                }
            } catch (ApiException e) {
                error.postValue("Error al añadir la dirección");
            }
        });
    }

    public void removeAddressFromUser(Direccion direccion) {
        executorService.execute(() -> {
            try {
                LoggedInUser loggedUser = loginRepository.getUser();
                if (loggedUser == null) return;

                User apiUser = userApi.getByEmail(loggedUser.getEmail());
                if (apiUser == null || apiUser.getId() == null) return;
                
                Set<Direccion> currentDirs = apiUser.getDirecciones();
                if (currentDirs != null) {
                    Set<Direccion> updatedDirs = currentDirs.stream()
                            .filter(d -> !d.getId().equals(direccion.getId()))
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    if (updatedDirs.size() < currentDirs.size()) {
                        apiUser.setDirecciones(updatedDirs);
                        userApi.updateUser(apiUser);
                        refreshUserData();
                    }
                }
            } catch (ApiException e) {
                error.postValue("Error al quitar la dirección");
            }
        });
    }

    public void updateAddress(Direccion updatedDir) {
        executorService.execute(() -> {
            try {
                Boolean success = direccionApi.updateDireccion(updatedDir);
                if (success != null && success) {
                    refreshUserData();
                    updateAddressResult.postValue(true);
                } else {
                    updateAddressResult.postValue(false);
                }
            } catch (ApiException e) {
                updateAddressResult.postValue(false);
            }
        });
    }

    public void registerAndAssignAddress(String poblacion, String calle, String cp, int portal, String piso) {
        executorService.execute(() -> {
            try {
                LoggedInUser loggedUser = loginRepository.getUser();
                if (loggedUser == null) return;

                // 1. Asegurar que tenemos al usuario con su ID correcto desde la API antes de proceder
                User apiUser = userApi.getByEmail(loggedUser.getEmail());
                if (apiUser == null || apiUser.getId() == null) {
                    Log.e("UserViewModel", "No se pudo obtener el usuario o el ID es nulo antes de registrar dirección");
                    error.postValue("Error al identificar al usuario");
                    return;
                }

                // 2. Crear y registrar la dirección
                Direccion d = new Direccion();
                d.setPoblacion(poblacion);
                d.setCalle(calle);
                d.setCodigoPostal(cp);
                d.setPortal(portal);
                d.setPiso(piso);

                Direccion savedDir = direccionApi.registerDireccion(d);
                if (savedDir != null && savedDir.getId() != null) {
                    // 3. Vincular la dirección al usuario (apiUser ya tiene ID verificado)
                    Set<Direccion> userDirs = apiUser.getDirecciones();
                    if (userDirs == null) userDirs = new LinkedHashSet<>();
                    
                    userDirs.add(savedDir);
                    apiUser.setDirecciones(userDirs);
                    
                    // 4. Actualizar el usuario en el servidor con el objeto completo (incluyendo su ID)
                    Boolean success = userApi.updateUser(apiUser);
                    if (success != null && success) {
                        refreshUserData();
                        updateAddressResult.postValue(true);
                    } else {
                        updateAddressResult.postValue(false);
                    }
                } else {
                    error.postValue("Error al registrar la dirección en el servidor");
                }
            } catch (ApiException e) {
                Log.e("UserViewModel", "Error al crear y asignar dirección: " + e.getMessage());
                error.postValue("Error al guardar la nueva dirección");
                updateAddressResult.postValue(false);
            }
        });
    }

    public void loadUserData(LoggedInUser user) {
        if (user == null) return;
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
                photoUri.setValue(user.getPhotoPath()); // Podría ser la ruta relativa del bucket
            }
        }
        
        if (user.getFavoritePlates() != null) {
            favoritos.setValue(user.getFavoritePlates());
        }

        if (user.getCartPlates() != null) {
            List<StagedBombo> cartMap = user.getCartPlates();
            carrito.setValue(cartMap);
        }
    }

    public LiveData<String> getUserId() { return userId; }
    public LiveData<String> getName() { return name; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getPassword() { return password; }
    public LiveData<String> getPhotoUri() { return photoUri; }
    public LiveData<List<Bombo>> getFavoritos() { return favoritos; }
    public LiveData<List<StagedBombo>> getCarrito() { return carrito; }
    public LiveData<List<String>> getAddresses() { return addresses; }
    public LiveData<List<Direccion>> getUserAddressesObjects() { return userAddressesObjects; }
    public LiveData<String> getError() { return error; }
    public LiveData<Result<Boolean>> getUpdateResult() { return updateResult; }
    public LiveData<Boolean> isAccountDeleted() { return accountDeleted; }

    private final MutableLiveData<Boolean> updateAddressResult = new MutableLiveData<>();

    public LiveData<Boolean> getUpdateAddressResult() { return updateAddressResult; }
    public void resetUpdateAddressResult() { updateAddressResult.postValue(null); }
    public void resetUpdateResult() { updateResult.setValue(null); }

    public void setName(String newName) {
        executorService.execute(() -> {
            Result<LoggedInUser> result = loginRepository.updateName(newName);
            if (result instanceof Result.Success) {
                name.postValue(newName);
                updateResult.postValue(new Result.Success<>(true));
            } else {
                updateResult.postValue(new Result.Error(((Result.Error) result).getError()));
            }
        });
    }

    public void setEmail(String newEmail) {
        executorService.execute(() -> {
            Result<LoggedInUser> result = loginRepository.updateEmail(newEmail);
            if (result instanceof Result.Success) {
                email.postValue(newEmail);
                sharedPreferences.edit().putString(KEY_CURRENT_USER_EMAIL, newEmail).apply();
                updateResult.postValue(new Result.Success<>(true));
            } else {
                updateResult.postValue(new Result.Error(((Result.Error) result).getError()));
            }
        });
    }

    public void setPassword(String oldPass, String newPass) {
        executorService.execute(() -> {
            Result<LoggedInUser> result = loginRepository.updatePassword(oldPass, newPass);
            if (result instanceof Result.Success) {
                password.postValue(newPass);
                updateResult.postValue(new Result.Success<>(true));
            } else {
                updateResult.postValue(new Result.Error(((Result.Error) result).getError()));
            }
        });
    }

    public File getUserPhotoFile() {
        return loginRepository.getUserPhotoFile();
    }

    public void setPhotoUri(String uri) {
        photoUri.postValue(uri);
        executorService.execute(() -> {
            LoggedInUser user = loginRepository.getUser();
            if (user != null) {
                user.setPhotoPath(uri);
                loginRepository.saveUser();
            }
        });
    }

    public void toggleFavorito(Bombo bombo) {
        List<Bombo> favoritos = this.favoritos.getValue();
        Log.e("", "esFavorito=" + esFavorito(bombo.getId()) + ", favoritos == null=" + (favoritos == null));
        if (esFavorito(bombo.getId())) {
            boolean borrao = favoritos.remove(bombo);
            Log.e("", "BORRANDO FAVORITO" + borrao);
        } else {
            favoritos.add(bombo);
            Log.w("", "AÑADIENDO VAORITOR");
        }
        this.favoritos.postValue(favoritos);
        this.loginRepository.setFavorites(favoritos);
    }

    public void setCarritoUI(List<StagedBombo> nuevoCarritoPlano) {
        carrito.setValue(new ArrayList<>(nuevoCarritoPlano));
        executorService.execute(() -> {
            loginRepository.setCartMap(this.carrito.getValue());
        });
    }

    @Override
    public boolean esFavorito(String bomboId) {
        List<Bombo> favoritos = this.favoritos.getValue();
        if (favoritos == null) return false;
        return favoritos.stream().map(Bombo::getId).anyMatch(id -> id.equalsIgnoreCase(bomboId));
    }

    public void clearError() { error.setValue(null); }

    public void deleteAccount() {
        executorService.execute(() -> {
            loginRepository.deleteAccount();
            sharedPreferences.edit().remove(KEY_CURRENT_USER_EMAIL).apply();
            accountDeleted.postValue(true);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
