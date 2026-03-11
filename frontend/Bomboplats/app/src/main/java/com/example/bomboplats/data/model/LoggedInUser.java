package com.example.bomboplats.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String email;
    private String password;
    private List<String> favoritePlateIds = new ArrayList<>();
    private Map<String, Integer> cartItems = new HashMap<>(); // Cambiado a Map para guardar ID y Cantidad

    public LoggedInUser(String userId, String displayName, String email, String password, List<String> favoritePlateIds, Map<String, Integer> cartItems) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.favoritePlateIds = favoritePlateIds != null ? favoritePlateIds : new ArrayList<>();
        this.cartItems = cartItems != null ? cartItems : new HashMap<>();
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

    public Map<String, Integer> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Map<String, Integer> cartItems) {
        this.cartItems = cartItems;
    }
}