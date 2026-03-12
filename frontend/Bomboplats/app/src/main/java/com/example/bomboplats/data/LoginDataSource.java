package com.example.bomboplats.data;

import android.content.Context;
import com.example.bomboplats.data.model.LoggedInUser;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class LoginDataSource {

    private final Context context;
    private final Gson gson;
    private final File usersDir;

    public LoginDataSource(Context context) {
        this.context = context;
        this.gson = new Gson();

        // Carpeta antigua y nueva
        File oldRoot = new File(context.getFilesDir(), "documents");
        File root = new File(context.getFilesDir(), "documentos");

        // Intentar migración si existe la carpeta antigua
        if (oldRoot.exists() && oldRoot.isDirectory()) {
            migrateFolder(oldRoot, root);
        }

        this.usersDir = new File(root, "users");
        if (!usersDir.exists()) usersDir.mkdirs();
        
        // Asegurar que los usuarios por defecto existen
        ensureDefaultUsers();
    }

    private void migrateFolder(File source, File target) {
        if (source.isDirectory()) {
            if (!target.exists()) target.mkdirs();
            String[] children = source.list();
            if (children != null) {
                for (String child : children) {
                    migrateFolder(new File(source, child), new File(target, child));
                }
            }
        } else {
            source.renameTo(target);
        }
        source.delete();
    }

    private void ensureDefaultUsers() {
        String[] emails = {"jorge@test.com", "usuario1@test.com", "usuario2@test.com"};
        String[] names = {"Jorge", "Usuario 1", "Usuario 2"};
        
        for (int i = 0; i < emails.length; i++) {
            File file = new File(usersDir, emails[i] + ".json");
            if (!file.exists()) {
                LoggedInUser user = new LoggedInUser(
                        String.valueOf(i + 1),
                        names[i],
                        emails[i],
                        "1234",
                        new ArrayList<>(),
                        new ArrayList<>(),
                        null
                );
                saveUserInternal(user);
            }
        }
    }

    public Result<LoggedInUser> login(String username, String password) {
        File file = new File(usersDir, username + ".json");
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
        File[] files = usersDir.listFiles((dir, name) -> name.endsWith(".json"));
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

    public Result<LoggedInUser> updateName(String username, String newName) {
        Result<LoggedInUser> loadResult = getUser(username);
        if (loadResult instanceof Result.Success) {
            LoggedInUser user = ((Result.Success<LoggedInUser>) loadResult).getData();
            user.setDisplayName(newName);
            return saveUserInternal(user);
        }
        return loadResult;
    }

    public Result<LoggedInUser> updateEmail(String oldUsername, String newEmail) {
        File[] files = usersDir.listFiles((dir, name) -> name.endsWith(".json"));
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
            
            // Mover también la foto si existe
            File oldPhoto = getUserPhotoFile(oldUsername);
            if (oldPhoto.exists()) {
                File newPhoto = getUserPhotoFile(newEmail);
                oldPhoto.renameTo(newPhoto);
            }

            File oldFile = new File(usersDir, oldUsername + ".json");
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
        try {
            if (file.exists()) {
                file.setWritable(true);
            }
            
            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter osw = new OutputStreamWriter(fos)) {
                gson.toJson(user, osw);
                osw.flush();
                fos.getFD().sync();
                return new Result.Success<>(user);
            }
        } catch (IOException e) {
            return new Result.Error(new IOException("Error al guardar usuario: " + e.getMessage(), e));
        }
    }

    public File getUserPhotoFile(String email) {
        return new File(usersDir, email + ".jpg");
    }

    public void logout() {}
}
