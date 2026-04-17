package com.example.bomboplats.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.LoginAttempt;
import com.example.bomboplats.api.Plato;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 * Migrated from local JSON storage to REST API.
 */
public class LoginDataSource {

    public static final String ERROR_EMAIL_ALREADY_EXISTS = "EMAIL_EXISTS";
    public static final String ERROR_USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String ERROR_WRONG_PASSWORD = "WRONG_PASSWORD";
    
    private static final String PREF_CART_NAME = "user_carts";
    private final UserControllerApi userControllerApi;
    private final Context context;
    private final Gson gson = new Gson();

    public LoginDataSource(Context context) {
        this.context = context;
        this.userControllerApi = new UserControllerApi();
    }

    public Result<LoggedInUser> login(String email, String password) {
        try {
            LoginAttempt attempt = new LoginAttempt();
            attempt.setEmail(email);
            attempt.setPassword(password);

            Boolean success = userControllerApi.login(attempt);
            if (success != null && success) {
                User apiUser = userControllerApi.getByEmail(email);
                return new Result.Success<>(convertToLoggedInUser(apiUser, password));
            } else {
                return new Result.Error(new IOException(ERROR_WRONG_PASSWORD));
            }
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                return new Result.Error(new IOException(ERROR_USER_NOT_FOUND));
            }
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
                return new Result.Success<>(convertToLoggedInUser(apiUser, user.getPassword()));
            }
            return new Result.Error(new IOException("Error al registrar usuario"));
        } catch (ApiException e) {
            if (e.getCode() == 409 || (e.getResponseBody() != null && e.getResponseBody().contains("exists"))) {
                return new Result.Error(new IOException(ERROR_EMAIL_ALREADY_EXISTS));
            }
            return new Result.Error(new IOException("Error en el registro: " + e.getMessage()));
        }
    }

    public Result<LoggedInUser> getUser(String email) {
        try {
            User apiUser = userControllerApi.getByEmail(email);
            if (apiUser != null) {
                return new Result.Success<>(convertToLoggedInUser(apiUser, null));
            }
            return new Result.Error(new IOException(ERROR_USER_NOT_FOUND));
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error al obtener usuario: " + e.getMessage()));
        }
    }

    /**
     * Sincroniza el usuario local con el servidor.
     * Importante: Recuperamos el objeto User actual de la API para no perder campos (direcciones, etc)
     * que no manejamos completamente en LoggedInUser.
     */
    public Result<LoggedInUser> saveUserInternal(LoggedInUser localUser) {
        try {
            saveCartLocally(localUser.getEmail(), localUser.getCartPlates());
            
            User apiUser = userControllerApi.getByEmail(localUser.getEmail());
            if (apiUser != null) {
                apiUser.setNickname(localUser.getDisplayName());
                apiUser.setIconUrl(localUser.getPhotoPath());
                
                // Actualizar platos favoritos
                java.util.Set<Plato> platos = new java.util.LinkedHashSet<>();
                for (List<String> ids : localUser.getFavoritePlates().values()) {
                    for (String id : ids) {
                        Plato p = new Plato();
                        p.setId(id);
                        platos.add(p);
                    }
                }
                apiUser.setPlatosFavoritos(platos);

                Boolean success = userControllerApi.updateUser(apiUser);
                if (success != null && success) {
                    return new Result.Success<>(localUser);
                }
            }
            return new Result.Error(new IOException("Error al actualizar usuario en el servidor"));
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error de red al guardar: " + e.getMessage()));
        }
    }

    private void saveCartLocally(String email, Map<String, List<String>> cart) {
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
                Boolean success = userControllerApi.updateUser(apiUser);
                if (success != null && success) {
                    return new Result.Success<>(convertToLoggedInUser(apiUser, null));
                }
            }
            return new Result.Error(new IOException("No se pudo actualizar el nombre"));
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error al actualizar nombre: " + e.getMessage()));
        }
    }

    public Result<LoggedInUser> updateEmail(String oldEmail, String newEmail) {
        try {
            User apiUser = userControllerApi.getByEmail(oldEmail);
            if (apiUser != null) {
                apiUser.setEmail(newEmail);
                Boolean success = userControllerApi.updateUser(apiUser);
                if (success != null && success) {
                    Map<String, List<String>> cart = loadCartLocally(oldEmail);
                    saveCartLocally(newEmail, cart);
                    return new Result.Success<>(convertToLoggedInUser(apiUser, null));
                }
            }
            return new Result.Error(new IOException("No se pudo actualizar el email"));
        } catch (ApiException e) {
            if (e.getCode() == 409 || (e.getResponseBody() != null && e.getResponseBody().contains("exists"))) {
                return new Result.Error(new IOException(ERROR_EMAIL_ALREADY_EXISTS));
            }
            return new Result.Error(new IOException("Error al actualizar email: " + e.getMessage()));
        }
    }

    public Result<LoggedInUser> updatePassword(String email, String oldPassword, String newPassword) {
        try {
            LoginAttempt attempt = new LoginAttempt();
            attempt.setEmail(email);
            attempt.setPassword(oldPassword);
            
            Boolean loginOk = userControllerApi.login(attempt);
            if (loginOk != null && loginOk) {
                User apiUser = userControllerApi.getByEmail(email);
                Boolean success = userControllerApi.updatePassword(apiUser.getId(), newPassword);
                if (success != null && success) {
                    return new Result.Success<>(convertToLoggedInUser(apiUser, newPassword));
                }
            }
            return new Result.Error(new IOException(ERROR_WRONG_PASSWORD));
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error al actualizar contraseña: " + e.getMessage()));
        }
    }

    public void deleteUser(String email) {
        try {
            User apiUser = userControllerApi.getByEmail(email);
            if (apiUser != null) {
                userControllerApi.deleteByID(apiUser.getId());
                context.getSharedPreferences(PREF_CART_NAME, Context.MODE_PRIVATE).edit().remove("cart_" + email).apply();
            }
        } catch (ApiException ignored) {
        }
    }

    public File getUserPhotoFile(String email) {
        File root = new File(context.getFilesDir(), "documentos/users");
        if (!root.exists()) root.mkdirs();
        return new File(root, email + ".jpg");
    }

    public void logout() {
    }

    private LoggedInUser convertToLoggedInUser(User apiUser, String password) {
        Map<String, List<String>> favs = new HashMap<>();
        if (apiUser.getPlatosFavoritos() != null) {
            FoodRepository foodRepo = FoodRepository.getInstance(context);
            for (Plato p : apiUser.getPlatosFavoritos()) {
                Bombo b = foodRepo.getBomboPorId(p.getId());
                String restId = (b != null) ? b.getRestauranteId() : "REST_UNKNOWN";
                favs.computeIfAbsent(restId, k -> new ArrayList<>()).add(p.getId());
            }
        }

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
