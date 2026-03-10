package com.example.bomboplats.data;

import com.example.bomboplats.data.model.LoggedInUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final Map<String, String> testAccounts = new HashMap<>();

    public LoginDataSource() {
        // Cuentas de prueba (deben coincidir con las de UserViewModel)
        testAccounts.put("usuario1@test.com", "juan123");
        testAccounts.put("usuario2@test.com", "maria456");
    }

    public Result<LoggedInUser> login(String username, String password) {
        try {
            if (testAccounts.containsKey(username) && testAccounts.get(username).equals(password)) {
                LoggedInUser user = new LoggedInUser(
                        java.util.UUID.randomUUID().toString(),
                        username.split("@")[0]); // Nombre basado en el email
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException("Usuario o contraseña incorrectos"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
