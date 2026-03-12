package com.example.bomboplats.data.model;

import java.util.ArrayList;
import java.util.List;

public class LoggedInUser {

    private String userId;
    private String displayName;
    private String email;
    private String password;
    private String photoPath;
    private List<String> favoritePlateIds = new ArrayList<>();
    private List<String> cartPlateIds = new ArrayList<>();

    public LoggedInUser(String userId, String displayName, String email, String password, List<String> favoritePlateIds, List<String> cartPlateIds, String photoPath) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.favoritePlateIds = favoritePlateIds != null ? favoritePlateIds : new ArrayList<>();
        this.cartPlateIds = cartPlateIds != null ? cartPlateIds : new ArrayList<>();
        this.photoPath = photoPath;
    }

    public String getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhotoPath() { return photoPath; }

    public List<String> getFavoritePlateIds() {
        if (favoritePlateIds == null) favoritePlateIds = new ArrayList<>();
        return favoritePlateIds;
    }

    public List<String> getCartPlateIds() {
        if (cartPlateIds == null) cartPlateIds = new ArrayList<>();
        return cartPlateIds;
    }

    public void setUserId(String userId) { this.userId = userId; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setFavoritePlateIds(List<String> ids) { this.favoritePlateIds = ids; }
    public void setCartPlateIds(List<String> ids) { this.cartPlateIds = ids; }
}
