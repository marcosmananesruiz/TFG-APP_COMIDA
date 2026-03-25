package com.example.bomboplats.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.LoginRepository;
import com.example.bomboplats.data.Result;
import com.example.bomboplats.data.model.Cuenta;
import com.example.bomboplats.data.model.LoggedInUser;
import com.example.bomboplats.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(Cuenta cuenta) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(cuenta.getEmail(), cuenta.getPassword());

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else if (result instanceof Result.Error) {
            String errorMsg = ((Result.Error) result).getError().getMessage();
            if (LoginDataSource.ERROR_WRONG_PASSWORD.equals(errorMsg)) {
                loginResult.setValue(new LoginResult(R.string.invalid_password));
            } else if (LoginDataSource.ERROR_USER_NOT_FOUND.equals(errorMsg)) {
                loginResult.setValue(new LoginResult(R.string.invalid_username));
            } else {
                loginResult.setValue(new LoginResult(R.string.login_failed));
            }
        }
    }

    public void registerTestUser(String email, String password, String displayName) {
        LoggedInUser newUser = new LoggedInUser(
                java.util.UUID.randomUUID().toString(),
                displayName,
                email,
                password,
                new HashMap<>(),
                new HashMap<>(),
                null
        );
        
        Result<LoggedInUser> result = loginRepository.register(newUser);
        if (result instanceof Result.Error) {
            String errorMsg = ((Result.Error) result).getError().getMessage();
            if (LoginDataSource.ERROR_EMAIL_ALREADY_EXISTS.equals(errorMsg)) {
                loginResult.setValue(new LoginResult(R.string.error_email_exists));
            } else {
                loginResult.setValue(new LoginResult(R.string.login_failed));
            }
        } else {
            loginResult.setValue(new LoginResult(new LoggedInUserView(displayName)));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
