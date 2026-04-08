package com.example.bomboplats.data;

import android.content.Context;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.LoginAttempt;
import com.example.bomboplats.api.Plato;
import com.example.bomboplats.api.User;
import com.example.bomboplats.api.UserControllerApi;
import com.example.bomboplats.api.UserRegister;
import com.example.bomboplats.data.model.LoggedInUser;
import java.io.File;
import java.io.IOException;
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

    private final UserControllerApi userControllerApi;
    private final Context context;

    public LoginDataSource(Context context) {
        this.context = context;
        this.userControllerApi = new UserControllerApi();
        // Nota: Asegúrate de que ApiClient tenga la URL correcta (ej. http://10.0.2.2:8080 para emulador)
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
                // Como no tenemos la contraseña en el GET de User, pasamos null o mantenemos la sesión
                return new Result.Success<>(convertToLoggedInUser(apiUser, null));
            }
            return new Result.Error(new IOException(ERROR_USER_NOT_FOUND));
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error al obtener usuario: " + e.getMessage()));
        }
    }

    public Result<LoggedInUser> saveUserInternal(LoggedInUser user) {
        try {
            User apiUser = convertToApiUser(user);
            Boolean success = userControllerApi.updateUser(apiUser);
            if (success != null && success) {
                return new Result.Success<>(user);
            }
            return new Result.Error(new IOException("Error al actualizar usuario en el servidor"));
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error de red al guardar: " + e.getMessage()));
        }
    }

    public Result<LoggedInUser> updateName(String email, String newName) {
        Result<LoggedInUser> current = getUser(email);
        if (current instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) current).getData();
            user.setDisplayName(newName);
            return saveUserInternal(user);
        }
        return current;
    }

    public Result<LoggedInUser> updateEmail(String oldEmail, String newEmail) {
        // La API actual parece no tener un rename de email directo que devuelva User, 
        // así que usamos updateUser cambiando el campo email.
        try {
            User apiUser = userControllerApi.getByEmail(oldEmail);
            apiUser.setEmail(newEmail);
            Boolean success = userControllerApi.updateUser(apiUser);
            if (success != null && success) {
                return new Result.Success<>(convertToLoggedInUser(apiUser, null));
            }
            return new Result.Error(new IOException("No se pudo actualizar el email"));
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error al actualizar email: " + e.getMessage()));
        }
    }

    public Result<LoggedInUser> updatePassword(String email, String oldPassword, String newPassword) {
        try {
            // Primero validamos el login antiguo
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
            return new Result.Error(new IOException("La contraseña antigua no es correcta"));
        } catch (ApiException e) {
            return new Result.Error(new IOException("Error al cambiar contraseña: " + e.getMessage()));
        }
    }

    public void deleteUser(String email) {
        try {
            User apiUser = userControllerApi.getByEmail(email);
            if (apiUser != null) {
                userControllerApi.deleteByID(apiUser.getId());
            }
        } catch (ApiException ignored) {
        }
    }

    public File getUserPhotoFile(String email) {
        // Las fotos ahora se gestionan vía URL (S3), 
        // pero mantenemos el método por compatibilidad de firma si es necesario.
        File root = new File(context.getFilesDir(), "documentos/users");
        return new File(root, email + ".jpg");
    }

    public void logout() {
        // Limpieza de sesión si fuera necesario
    }

    // --- MÉTODOS DE CONVERSIÓN (EL TRADUCTOR) ---

    private LoggedInUser convertToLoggedInUser(User apiUser, String password) {
        Map<String, List<String>> favs = new HashMap<>();
        if (apiUser.getPlatosFavoritos() != null) {
            for (Plato p : apiUser.getPlatosFavoritos()) {
                // Aquí asumimos que el restauranteId está en el plato. 
                // Si la API no lo da, necesitaremos otro campo o un ID compuesto.
                // Como parche de compatibilidad, si p.getRestauranteId() no existe, 
                // tendrías que ver cómo guardas esa relación en la BD.
                String restId = "RESTAURANTE_DESCONOCIDO"; // TODO: Ajustar según modelo real de Plato
                favs.computeIfAbsent(restId, k -> new ArrayList<>()).add(p.getId());
            }
        }

        LoggedInUser user = new LoggedInUser(
                apiUser.getId(),
                apiUser.getNickname(),
                apiUser.getEmail(),
                password,
                favs,
                new HashMap<>(), // El carrito suele ser volátil, o se puede mapear igual
                apiUser.getIconUrl()
        );
        
        return user;
    }

    private User convertToApiUser(LoggedInUser localUser) {
        User apiUser = new User();
        apiUser.setId(localUser.getUserId());
        apiUser.setNickname(localUser.getDisplayName());
        apiUser.setEmail(localUser.getEmail());
        apiUser.setIconUrl(localUser.getPhotoPath());
        
        // Para los favoritos, convertimos el mapa de vuelta a Set de objetos Plato
        // Nota: Solo enviamos los IDs para que el servidor los vincule.
        // Si el servidor requiere el objeto completo, habrá que hidratarlo.
        /*
        Set<Plato> platos = new java.util.LinkedHashSet<>();
        for (List<String> ids : localUser.getFavoritePlates().values()) {
            for (String id : ids) {
                Plato p = new Plato();
                p.setId(id);
                platos.add(p);
            }
        }
        apiUser.setPlatosFavoritos(platos);
        */
        
        return apiUser;
    }
}
