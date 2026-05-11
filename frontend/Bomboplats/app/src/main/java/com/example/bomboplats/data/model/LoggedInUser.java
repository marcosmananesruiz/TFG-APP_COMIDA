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

    private List<Bombo> favoritePlates;
    private List<StagedBombo> cartPlates = new ArrayList<>();
    private List<Pedido> orderHistory = new ArrayList<>();
    private List<String> addresses = new ArrayList<>();

    public LoggedInUser(String userId, String displayName, String email, String password, List<Bombo> favoritePlates, List<StagedBombo> cartPlates, String photoPath) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.favoritePlates = favoritePlates;
        this.cartPlates = cartPlates != null ? cartPlates : new ArrayList<>();
        this.orderHistory = new ArrayList<>();
        this.addresses = new ArrayList<>();
        this.photoPath = photoPath;
    }

    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhotoPath() { return photoPath; }

    public List<Bombo> getFavoritePlates() {
        if (favoritePlates == null) favoritePlates = new ArrayList<>();
        return favoritePlates;
    }

    public List<StagedBombo> getCartPlates() {
        if (cartPlates == null) cartPlates = new ArrayList<>();
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
    public void setFavoritePlates(List<Bombo> favoritePlates) { this.favoritePlates = favoritePlates; }
    public void setCartPlates(List<StagedBombo> cartPlates) { this.cartPlates = cartPlates; }
    public void setOrderHistory(List<Pedido> orderHistory) { this.orderHistory = orderHistory; }
    public void setAddresses(List<String> addresses) { this.addresses = addresses; }
}
