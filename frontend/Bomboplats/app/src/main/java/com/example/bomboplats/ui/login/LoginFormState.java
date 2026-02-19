package com.example.bomboplats.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState { // Comprueba que los datos sean válidos.
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    private boolean isDataValid;

    // Guarda el id del usuario y de su contraseña en caso de que haya error
    LoginFormState(@Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }
    // Cuando los datos son válidos, nullea los campos
    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}