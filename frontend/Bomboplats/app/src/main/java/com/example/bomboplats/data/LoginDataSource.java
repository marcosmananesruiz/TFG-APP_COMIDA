package com.example.bomboplats.data;

import android.content.Context;
import com.example.bomboplats.data.model.LoggedInUser;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 * Persistence is managed via local JSON files using GSON.
 */
public class LoginDataSource {

    private final Context context;
    private final Gson gson;

    public LoginDataSource(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public Result<LoggedInUser> login(String username, String password) {
        File file = new File(context.getFilesDir(), username + ".json");
        if (!file.exists()) {
            return new Result.Error(new IOException("Usuario no encontrado"));
        }

        try (FileReader reader = new FileReader(file)) {
            LoggedInUser user = gson.fromJson(reader, LoggedInUser.class);
            if (user != null && user.getPassword().equals(password)) {
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException("Contraseña incorrecta"));
            }
        } catch (IOException e) {
            return new Result.Error(new IOException("Error al leer datos del usuario", e));
        }
    }

    public Result<LoggedInUser> register(LoggedInUser user) {
        // Verificar que el email no esté ya registrado en otra cuenta
        File filesDir = context.getFilesDir();
        File[] files = filesDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File f : files) {
                try (FileReader reader = new FileReader(f)) {
                    LoggedInUser existingUser = gson.fromJson(reader, LoggedInUser.class);
                    if (existingUser != null && existingUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                        return new Result.Error(new IOException("El email ya está registrado"));
                    }
                } catch (IOException ignored) {}
            }
        }

        return saveUserInternal(user);
    }

    public Result<LoggedInUser> updateEmail(String oldUsername, String newEmail) {
        // Verificar que el nuevo email no exista en otros archivos
        File filesDir = context.getFilesDir();
        File[] files = filesDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File f : files) {
                try (FileReader reader = new FileReader(f)) {
                    LoggedInUser existingUser = gson.fromJson(reader, LoggedInUser.class);
                    if (existingUser != null && existingUser.getEmail().equalsIgnoreCase(newEmail)) {
                        return new Result.Error(new IOException("El nuevo email ya está en uso"));
                    }
                } catch (IOException ignored) {}
            }
        }

        Result<LoggedInUser> loadResult = getUser(oldUsername);
        if (loadResult instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) loadResult).getData();
            user.setEmail(newEmail);
            // Borrar el archivo viejo y guardar el nuevo (nombre de archivo basado en el username/email)
            File oldFile = new File(context.getFilesDir(), oldUsername + ".json");
            oldFile.delete();
            return saveUserInternal(user);
        }
        return loadResult;
    }

    public Result<LoggedInUser> updatePassword(String username, String oldPassword, String newPassword) {
        Result<LoggedInUser> loadResult = login(username, oldPassword);
        if (loadResult instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) loadResult).getData();
            user.setPassword(newPassword);
            return saveUserInternal(user);
        }
        return new Result.Error(new IOException("La contraseña antigua no es correcta"));
    }

    public void addFavorite(String username, String plateId) {
        Result<LoggedInUser> loadResult = getUser(username);
        if (loadResult instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) loadResult).getData();
            if (!user.getFavoritePlateIds().contains(plateId)) {
                user.getFavoritePlateIds().add(plateId);
                saveUserInternal(user);
            }
        }
    }

    public void removeFavorite(String username, String plateId) {
        Result<LoggedInUser> loadResult = getUser(username);
        if (loadResult instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) loadResult).getData();
            user.getFavoritePlateIds().remove(plateId);
            saveUserInternal(user);
        }
    }

    public void addToCart(String username, String plateId) {
        Result<LoggedInUser> loadResult = getUser(username);
        if (loadResult instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) loadResult).getData();
            user.getCartPlateIds().add(plateId);
            saveUserInternal(user);
        }
    }

    public void removeFromCart(String username, String plateId) {
        Result<LoggedInUser> loadResult = getUser(username);
        if (loadResult instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) loadResult).getData();
            user.getCartPlateIds().remove(plateId);
            saveUserInternal(user);
        }
    }

    private Result<LoggedInUser> getUser(String username) {
        File file = new File(context.getFilesDir(), username + ".json");
        if (!file.exists()) return new Result.Error(new IOException("Usuario no encontrado"));
        try (FileReader reader = new FileReader(file)) {
            LoggedInUser user = gson.fromJson(reader, LoggedInUser.class);
            return new Result.Success<>(user);
        } catch (IOException e) {
            return new Result.Error(e);
        }
    }

    private Result<LoggedInUser> saveUserInternal(LoggedInUser user) {
        // Usamos el email como identificador para el nombre del archivo (username.json)
        File file = new File(context.getFilesDir(), user.getEmail() + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(user, writer);
            return new Result.Success<>(user);
        } catch (IOException e) {
            return new Result.Error(new IOException("Error al guardar usuario", e));
        }
    }

    public void logout() {
        // Revoke authentication
    }
}
