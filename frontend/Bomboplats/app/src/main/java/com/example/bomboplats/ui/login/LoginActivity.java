package com.example.bomboplats.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.UserControllerApi;
import com.example.bomboplats.data.model.Cuenta;
import com.example.bomboplats.databinding.ActivityLoginBinding;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private long backPressedTime;
    private Toast backToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- COMPROBACIÓN DE SESIÓN (TOKEN SIMULADO) ---
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean keepForever = prefs.getBoolean("keep_session_forever", false);
        long expirationTime = prefs.getLong("session_expiration", 0);
        String currentEmail = prefs.getString("current_user_email", null);

        if (currentEmail != null && (keepForever || expirationTime > System.currentTimeMillis())) {
            // La sesión sigue siendo válida (o es indefinida)
            Intent intent = new Intent(this, GeneralActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // ----------------------------------------------

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory(getApplicationContext()))
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final CheckBox keepSessionCheckBox = binding.keepSession;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final ImageView catImageView = binding.imageView;
        final TextView registerLink = binding.registerLink;

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    if (backToast != null) {
                        backToast.cancel();
                    }
                    finishAffinity();
                    return;
                }
                backToast = Toast.makeText(LoginActivity.this, getString(R.string.atras_salir), Toast.LENGTH_SHORT);
                backToast.show();
                backPressedTime = System.currentTimeMillis();
            }
        });

        // IMAGEN DE GATO
        catImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.registerTestUser("usuario1@test.com", "juan123", "Juan Pérez");
                loginViewModel.registerTestUser("usuario2@test.com", "maria456", "María García");

                usernameEditText.setText("usuario1@test.com");
                passwordEditText.setText("juan123");
                
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(new Cuenta("usuario1@test.com", "juan123"));

                /*
                // 👇 Mover la llamada de red a un hilo secundario
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    try {
                        ApiClient client = new ApiClient();
                        client.setBasePath("http://10.0.2.2:8080");
                        UserControllerApi controllerApi = new UserControllerApi(client);

                        controllerApi.findAll().forEach(user ->
                                Log.d("API", user.toString())
                        );

                    } catch (ApiException e) {
                        Log.e("API", "Error al obtener usuarios: " + e.getMessage());
                    }
                });

                 */
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    String email = usernameEditText.getText().toString();
                    boolean shouldKeepForever = keepSessionCheckBox.isChecked();
                    
                    // --- GUARDAR SESIÓN (TOKEN SIMULADO) ---
                    SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    
                    editor.putString("current_user_email", email);
                    editor.putBoolean("keep_session_forever", shouldKeepForever);

                    if (!shouldKeepForever) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, 7); // Añadir una semana
                        editor.putLong("session_expiration", calendar.getTimeInMillis());
                    } else {
                        editor.remove("session_expiration");
                    }
                    
                    editor.apply();
                    // ----------------------------------------

                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);

                    Intent generalIntent = new Intent(LoginActivity.this, GeneralActivity.class);
                    startActivity(generalIntent);
                    finish();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(new Cuenta(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString()));
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(new Cuenta(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString()));
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome, model.getDisplayName());
        Toast.makeText(LoginActivity.this, welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(LoginActivity.this, errorString, Toast.LENGTH_SHORT).show();
    }
}
