package com.example.bomboplats.data.model;

import com.example.bomboplats.ui.historial.Pedido;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggedInUser {

    private String userId;
    private String displayName;
    private String email;
    private String password;
    private String photoPath;
    private Map<String, List<String>> favoritePlates = new HashMap<>();
    private Map<String, List<String>> cartPlates = new HashMap<>();
    private List<Pedido> orderHistory = new ArrayList<>();
    private List<String> addresses = new ArrayList<>();

    public LoggedInUser(String userId, String displayName, String email, String password, Map<String, List<String>> favoritePlates, Map<String, List<String>> cartPlates, String photoPath) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.favoritePlates = favoritePlates != null ? favoritePlates : new HashMap<>();
        this.cartPlates = cartPlates != null ? cartPlates : new HashMap<>();
        this.orderHistory = new ArrayList<>();
        this.addresses = new ArrayList<>();
        this.photoPath = photoPath;
    }

    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhotoPath() { return photoPath; }

    public Map<String, List<String>> getFavoritePlates() {
        if (favoritePlates == null) favoritePlates = new HashMap<>();
        return favoritePlates;
    }

    public Map<String, List<String>> getCartPlates() {
        if (cartPlates == null) cartPlates = new HashMap<>();
        return cartPlates;
    }

    public List<Pedido> getOrderHistory() {
        if (orderHistory == null) orderHistory = new ArrayList<>();
        return orderHistory;
    }

    public List<String> getAddresses() {
        if (addresses == null) addresses = new ArrayList<>();
        return addresses;
    }

    public void setUserId(String userId) { this.userId = userId; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setFavoritePlates(Map<String, List<String>> favoritePlates) { this.favoritePlates = favoritePlates; }
    public void setCartPlates(Map<String, List<String>> cartPlates) { this.cartPlates = cartPlates; }
    public void setOrderHistory(List<Pedido> orderHistory) { this.orderHistory = orderHistory; }
    public void setAddresses(List<String> addresses) { this.addresses = addresses; }
}
