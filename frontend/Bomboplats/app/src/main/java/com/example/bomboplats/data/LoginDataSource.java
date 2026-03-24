package com.example.bomboplats.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.bomboplats.data.model.LoggedInUser;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class LoginDataSource {

    private final Context context;
    private final Gson gson;
    private final File usersDir;
    private static final String PREFS_NAME = "login_prefs";
    private static final String KEY_DEFAULTS_CREATED = "defaults_created";

    public LoginDataSource(Context context) {
        this.context = context;
        this.gson = new Gson();

        File root = new File(context.getFilesDir(), "documentos");
        this.usersDir = new File(root, "users");
        if (!usersDir.exists()) usersDir.mkdirs();
        
        ensureDefaultUsers();
    }

    private void ensureDefaultUsers() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_DEFAULTS_CREATED, false)) return;

        String[] emails = {"jorge@test.com", "usuario1@test.com", "usuario2@test.com"};
        String[] names = {"Jorge", "Usuario 1", "Usuario 2"};
        
        for (int i = 0; i < emails.length; i++) {
            File file = new File(usersDir, emails[i] + ".json");
            if (!file.exists()) {
                LoggedInUser user = new LoggedInUser(
                        String.valueOf(i + 1),
                        names[i],
                        emails[i],
                        "jorge123",
                        new HashMap<>(),
                        new HashMap<>(),
                        null
                );
                saveUserInternal(user);
            }
        }
        prefs.edit().putBoolean(KEY_DEFAULTS_CREATED, true).apply();
    }

    public Result<LoggedInUser> login(String username, String password) {
        File file = new File(usersDir, username + ".json");
        if (!file.exists()) return new Result.Error(new IOException("Usuario no encontrado"));

        try (FileReader reader = new FileReader(file)) {
            LoggedInUser user = gson.fromJson(reader, LoggedInUser.class);
            if (user != null && user.getPassword().equals(password)) {
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException("Contraseña incorrecta"));
            }
        } catch (IOException e) {
            return new Result.Error(e);
        }
    }

    public Result<LoggedInUser> updateEmail(String oldEmail, String newEmail) {
        // Verificar si el nuevo email ya existe
        if (new File(usersDir, newEmail + ".json").exists()) {
            return new Result.Error(new IOException("El nuevo correo ya está registrado"));
        }

        Result<LoggedInUser> loadResult = getUser(oldEmail);
        if (loadResult instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) loadResult).getData();
            File existingFile = new File(usersDir, oldEmail + ".json");

            user.setEmail(newEmail);
            
            try (FileOutputStream fos = new FileOutputStream(existingFile);
                 OutputStreamWriter osw = new OutputStreamWriter(fos)) {
                gson.toJson(user, osw);
                osw.flush();
                fos.getFD().sync();
            } catch (IOException e) {
                return new Result.Error(new IOException("Error al editar el archivo", e));
            }

            File newFile = new File(usersDir, newEmail + ".json");
            if (existingFile.renameTo(newFile)) {
                File oldHistory = new File(usersDir, oldEmail + "_history.json");
                if (oldHistory.exists()) {
                    oldHistory.renameTo(new File(usersDir, newEmail + "_history.json"));
                }
                File oldPhoto = new File(usersDir, oldEmail + ".jpg");
                if (oldPhoto.exists()) {
                    oldPhoto.renameTo(new File(usersDir, newEmail + ".jpg"));
                }
                return new Result.Success<>(user);
            }
            return new Result.Success<>(user);
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

    public Result<LoggedInUser> getUser(String username) {
        File file = new File(usersDir, username + ".json");
        if (!file.exists()) return new Result.Error(new IOException("Usuario no encontrado"));
        try (FileReader reader = new FileReader(file)) {
            LoggedInUser user = gson.fromJson(reader, LoggedInUser.class);
            return new Result.Success<>(user);
        } catch (IOException e) {
            return new Result.Error(e);
        }
    }

    public Result<LoggedInUser> saveUserInternal(LoggedInUser user) {
        File file = new File(usersDir, user.getEmail() + ".json");
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            gson.toJson(user, osw);
            osw.flush();
            fos.getFD().sync();
            return new Result.Success<>(user);
        } catch (IOException e) {
            return new Result.Error(new IOException("Error al guardar: " + e.getMessage()));
        }
    }

    public Result<LoggedInUser> updateName(String username, String newName) {
        Result<LoggedInUser> loadResult = getUser(username);
        if (loadResult instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) loadResult).getData();
            user.setDisplayName(newName);
            return saveUserInternal(user);
        }
        return loadResult;
    }

    public Result<LoggedInUser> register(LoggedInUser user) {
        File file = new File(usersDir, user.getEmail() + ".json");
        if (file.exists()) {
            return new Result.Error(new IOException("USUARIO_EXISTE"));
        }
        return saveUserInternal(user);
    }

    public File getUserPhotoFile(String email) {
        return new File(usersDir, email + ".jpg");
    }

    public void logout() {}
}
