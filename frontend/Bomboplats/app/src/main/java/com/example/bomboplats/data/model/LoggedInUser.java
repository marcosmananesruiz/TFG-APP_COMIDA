package com.example.bomboplats.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String email;
    private String password;
    private List<String> favoritePlateIds = new ArrayList<>();
    private List<String> cartPlateIds = new ArrayList<>();

    public LoggedInUser(String userId, String displayName, String email, String password, List<String> favoritePlateIds, List<String> cartPlateIds) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.favoritePlateIds = favoritePlateIds != null ? favoritePlateIds : new ArrayList<>();
        this.cartPlateIds = cartPlateIds != null ? cartPlateIds : new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getFavoritePlateIds() {
        return favoritePlateIds;
    }

    public void setFavoritePlateIds(List<String> favoritePlateIds) {
        this.favoritePlateIds = favoritePlateIds;
    }

    public List<String> getCartPlateIds() {
        return cartPlateIds;
    }

    public void setCartPlateIds(List<String> cartPlateIds) {
        this.cartPlateIds = cartPlateIds;
    }
}