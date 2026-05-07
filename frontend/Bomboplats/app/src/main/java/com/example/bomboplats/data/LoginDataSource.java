package com.example.bomboplats.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.LoginAttempt;
import com.example.bomboplats.api.Plato;
import com.example.bomboplats.api.PlatoControllerApi;
import com.example.bomboplats.api.User;
import com.example.bomboplats.api.UserControllerApi;
import com.example.bomboplats.api.UserRegister;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.LoggedInUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginDataSource {

    public static final String ERROR_EMAIL_ALREADY_EXISTS = "EMAIL_EXISTS";
    public static final String ERROR_USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String ERROR_WRONG_PASSWORD = "WRONG_PASSWORD";
    
    private static final String PREF_CART_NAME = "user_carts";
    private static final String PREF_USER_DATA = "user_profiles";
    
    private final UserControllerApi userControllerApi;
    private final Context context;
    private final Gson gson = new Gson();

    public LoginDataSource(Context context) {
        this.context = context;
        this.userControllerApi = new UserControllerApi();
    }

    private void saveProfileOffline(LoggedInUser user) {
        if (user == null || user.getEmail() == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_USER_DATA, Context.MODE_PRIVATE);
        
        // Regla de negocio: Solo guardamos datos de cuenta de forma persistente offline
        LoggedInUser profileOnly = new LoggedInUser(
                user.getUserId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPassword(),
                user.getFavoritePlates(), // No cacheamos favoritos offline por ahora
                null, // No cacheamos carrito offline por ahora
                user.getPhotoPath()
        );
        profileOnly.setAddresses(user.getAddresses());
        
        prefs.edit().putString("profile_" + user.getEmail(), gson.toJson(profileOnly)).apply();
    }

    private LoggedInUser loadProfileOffline(String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_USER_DATA, Context.MODE_PRIVATE);
        String json = prefs.getString("profile_" + email, null);
        if (json != null) {
            try {
                return gson.fromJson(json, LoggedInUser.class);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public Result<LoggedInUser> login(String email, String password) {
        try {
            LoginAttempt attempt = new LoginAttempt();
            attempt.setEmail(email);
            attempt.setPassword(password);

            Boolean success = userControllerApi.login(attempt);
            if (success != null && success) {
                User apiUser = userControllerApi.getByEmail(email);
                LoggedInUser user = convertToLoggedInUser(apiUser, password);
                saveProfileOffline(user);
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException(ERROR_WRONG_PASSWORD));
            }
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error de red: " + e.getMessage()));
        }
    }

    public Result<LoggedInUser> register(LoggedInUser user) {
        try {
            UserRegister registerData = new UserRegister();
            registerData.setEmail(user.getEmail());
            registerData.setNickname(user.getDisplayName());
            registerData.setPassword(user.getPassword());

            User apiUser = userControllerApi.registerUser(registerData);
            if (apiUser != null) {
                LoggedInUser registeredUser = convertToLoggedInUser(apiUser, user.getPassword());
                saveProfileOffline(registeredUser);
                return new Result.Success<>(registeredUser);
            }
            return new Result.Error(new IOException("Error al registrar usuario"));
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error en el registro: " + e.getMessage()));
        }
    }

    public Result<LoggedInUser> getUser(String email) {
        try {
            User apiUser = userControllerApi.getByEmail(email);
            if (apiUser != null) {
                LoggedInUser user = convertToLoggedInUser(apiUser, null);
                saveProfileOffline(user);
                return new Result.Success<>(user);
            }
        } catch (ApiException e) {
            // Offline fallback: Cargar perfil de SharedPreferences
            LoggedInUser offline = loadProfileOffline(email);
            if (offline != null) {
                return new Result.Success<>(offline);
            }
        }
        return new Result.Error(new IOException(ERROR_USER_NOT_FOUND));
    }

    public Result<LoggedInUser> saveUserInternal(LoggedInUser localUser) {
        saveProfileOffline(localUser);
        saveCartLocally(localUser.getEmail(), localUser.getCartPlates());
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                User apiUser = userControllerApi.getByEmail(localUser.getEmail());
                if (apiUser != null) {
                    apiUser.setNickname(localUser.getDisplayName());
                    apiUser.setIconUrl(localUser.getPhotoPath());

                    // Sincronizar favoritos con API
                /*java.util.Set<Plato> platos = new java.util.LinkedHashSet<>();
                if (localUser.getFavoritePlates() != null) {
                    for (List<String> ids : localUser.getFavoritePlates().values()) {
                        for (String id : ids) {
                            Plato p = new Plato();
                            p.setId(id);
                            platos.add(p);
                        }
                    }
                }*/
                    PlatoControllerApi platosApi = new PlatoControllerApi();
                    Set<Plato> platos = new HashSet<>();
                    localUser.getFavoritePlates().forEach(bombo -> {
                        try {
                            Plato plato = platosApi.getPlatoById(bombo.getId());
                            platos.add(plato);
                        } catch (ApiException e) {}
                    });

                    apiUser.setPlatosFavoritos(platos);

                    userControllerApi.updateUser(apiUser);
                }
            } catch (ApiException ignored) {}
        });
        return new Result.Success<>(localUser);
    }

    private void saveCartLocally(String email, Map<String, List<String>> cart) {
        if (cart == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_CART_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("cart_" + email, gson.toJson(cart)).apply();
    }

    private Map<String, List<String>> loadCartLocally(String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_CART_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString("cart_" + email, null);
        if (json != null) {
            Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new HashMap<>();
    }

    public Result<LoggedInUser> updateName(String email, String newName) {
        try {
            User apiUser = userControllerApi.getByEmail(email);
            if (apiUser != null) {
                apiUser.setNickname(newName);
                userControllerApi.updateUser(apiUser);
                LoggedInUser user = convertToLoggedInUser(apiUser, null);
                saveProfileOffline(user);
                return new Result.Success<>(user);
            }
        } catch (ApiException ignored) {}
        return new Result.Error(new IOException("Error al actualizar"));
    }

    public Result<LoggedInUser> updateEmail(String oldEmail, String newEmail) {
        try {
            User apiUser = userControllerApi.getByEmail(oldEmail);
            if (apiUser != null) {
                apiUser.setEmail(newEmail);
                userControllerApi.updateUser(apiUser);
                LoggedInUser user = convertToLoggedInUser(apiUser, null);
                saveProfileOffline(user);
                // Migrar carrito local
                Map<String, List<String>> cart = loadCartLocally(oldEmail);
                saveCartLocally(newEmail, cart);
                // Borrar perfil viejo
                context.getSharedPreferences(PREF_USER_DATA, Context.MODE_PRIVATE).edit().remove("profile_" + oldEmail).apply();
                return new Result.Success<>(user);
            }
        } catch (ApiException ignored) {}
        return new Result.Error(new IOException("Error al actualizar email"));
    }

    public Result<LoggedInUser> updatePassword(String email, String oldPassword, String newPassword) {
        try {
            LoginAttempt attempt = new LoginAttempt();
            attempt.setEmail(email);
            attempt.setPassword(oldPassword);
            if (Boolean.TRUE.equals(userControllerApi.login(attempt))) {
                User apiUser = userControllerApi.getByEmail(email);
                userControllerApi.updatePassword(apiUser.getId(), newPassword);
                return new Result.Success<>(convertToLoggedInUser(apiUser, newPassword));
            }
        } catch (ApiException ignored) {}
        return new Result.Error(new IOException(ERROR_WRONG_PASSWORD));
    }

    public void deleteUser(String email) {
        try {
            User apiUser = userControllerApi.getByEmail(email);
            if (apiUser != null) {
                userControllerApi.deleteByID(apiUser.getId());
                context.getSharedPreferences(PREF_USER_DATA, Context.MODE_PRIVATE).edit().remove("profile_" + email).apply();
            }
        } catch (ApiException ignored) {}
    }

    public File getUserPhotoFile(String email) {
        File root = new File(context.getFilesDir(), "documentos/users");
        if (!root.exists()) root.mkdirs();
        return new File(root, email + ".jpg");
    }

    public void logout() {}

    private LoggedInUser convertToLoggedInUser(User apiUser, String password) {
        List<Bombo> favs = new ArrayList<>();

        apiUser.getPlatosFavoritos().forEach(plato -> {
            Bombo bombo = new Bombo(plato);
            favs.add(bombo);
        });

        return new LoggedInUser(
                apiUser.getId(),
                apiUser.getNickname(),
                apiUser.getEmail(),
                password,
                favs,
                loadCartLocally(apiUser.getEmail()),
                apiUser.getIconUrl()
        );
    }
}
