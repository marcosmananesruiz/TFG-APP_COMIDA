package com.example.bomboplats.data;

import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.LoggedInUser;
import com.example.bomboplats.data.model.StagedBombo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginRepository {

    private static volatile LoginRepository instance;
    private LoginDataSource dataSource;
    private LoggedInUser user = null;

    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        } else if (dataSource != null) {
            instance.dataSource = dataSource;
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public Result<LoggedInUser> login(String username, String password) {
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public Result<LoggedInUser> register(LoggedInUser user) {
        Result<LoggedInUser> result = dataSource.register(user);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public boolean loadUserSession(String email) {
        if (email == null) return false;
        Result<LoggedInUser> result = dataSource.getUser(email);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
            return true;
        }
        return false;
    }

    public Result<LoggedInUser> updateName(String newName) {
        if (user == null) return new Result.Error(new Exception("No hay usuario logueado"));
        Result<LoggedInUser> result = dataSource.updateName(user.getEmail(), newName);
        if (result instanceof Result.Success) {
            user = ((Result.Success<LoggedInUser>) result).getData();
        }
        return result;
    }

    public Result<LoggedInUser> updateEmail(String newEmail) {
        if (user == null) return new Result.Error(new Exception("No hay usuario logueado"));
        String oldEmail = user.getEmail();
        Result<LoggedInUser> result = dataSource.updateEmail(oldEmail, newEmail);
        if (result instanceof Result.Success) {
            user = ((Result.Success<LoggedInUser>) result).getData();
        }
        return result;
    }

    public Result<LoggedInUser> updatePassword(String oldPassword, String newPassword) {
        if (user == null) return new Result.Error(new Exception("No hay usuario logueado"));
        Result<LoggedInUser> result = dataSource.updatePassword(user.getEmail(), oldPassword, newPassword);
        if (result instanceof Result.Success) {
            user = ((Result.Success<LoggedInUser>) result).getData();
        }
        return result;
    }

    public void saveUser() {
        if (user != null) {
            dataSource.saveUserInternal(user);
        }
    }

    public void setFavorites(List<Bombo> favoritePlates) {
        if (user != null) {
            user.setFavoritePlates(new ArrayList<>(favoritePlates));
            saveUser();
        }
    }

    public void setCartMap(List<StagedBombo> cartPlates) {
        if (user != null) {
            user.setCartPlates(new ArrayList<>(cartPlates));
            saveUser();
        }
    }

    public LoggedInUser getUser() {
        return user;
    }

    public File getUserPhotoFile() {
        if (user == null) return null;
        return dataSource.getUserPhotoFile(user.getEmail());
    }

    public void deleteAccount() {
        if (user != null) {
            dataSource.deleteUser(user.getEmail());
            user = null;
        }
    }
}
