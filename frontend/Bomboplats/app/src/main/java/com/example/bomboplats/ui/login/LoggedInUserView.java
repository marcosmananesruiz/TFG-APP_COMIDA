package com.example.bomboplats.ui.login;

/**
 * Clase LoggedInUserView para el nombre del usuario.
 */
class LoggedInUserView {
    private String displayName;

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}