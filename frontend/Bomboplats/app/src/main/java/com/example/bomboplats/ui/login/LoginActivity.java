package com.example.bomboplats.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.example.bomboplats.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private long backPressedTime;
    private Toast backToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inicia en modo claro forzado
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        // Inicializar movidas
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final ImageView catImageView = binding.imageView;

        // Manejar el botón de "atrás"
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
                backToast = Toast.makeText(LoginActivity.this, "Pulsa otra vez para salir", Toast.LENGTH_SHORT);
                backToast.show();
                backPressedTime = System.currentTimeMillis();
            }
        });
        // Esto es lo de que el gato te mueva al generalActivity
        catImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, GeneralActivity.class);
                startActivity(intent);
                finish(); // Cierra LoginActivity
            }
        });

        // Observa los datos del formulario
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override // Comprueba los cambios que se hagan en los campos
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid()); // Este comrueba si los datos son válidos
                if (loginFormState.getUsernameError() != null) {    // Comprueba con una funcion del loginFormState si hay errores con el nombre
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) { // Comprueba con una funcion del loginFormState si hay errores con la contraseña
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });
        // Aquí comprueba el resultado de iniciar sesión
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) { // Comprueba si tiene algún error el login
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) { // Comprueba si es un exito el login
                    updateUiWithUser(loginResult.getSuccess()); // Muestra un toast
                    setResult(Activity.RESULT_OK); // Se indica que se ha iniciado sesión correctamente

                    //Navega a la actividad principal y cierra esta
                    Intent generalIntent = new Intent(LoginActivity.this, GeneralActivity.class);
                    startActivity(generalIntent);
                    finish(); // Cierra LoginActivity
                }
            }
        });
        // Carga cuando detecta que se ha detectado un cambio del texto
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) { // Se ejecuta después de que se cambie el texto
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString()); // Manda los nuevos campos escritos y los registra como datos para que lo lea la app
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener); // Notifica que ha cambiado el texto
        passwordEditText.addTextChangedListener(afterTextChangedListener); // Notifica que ha cambiado el texto
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { // Detecta si se pulsa enviar
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });
        // Ejecuta lo que tenga dentro al pulsar el botón login.
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE); // Se muestra una pantalla de carga
                loginViewModel.login(usernameEditText.getText().toString(), // Se ejecuta la función de login de LoginViewModel
                        passwordEditText.getText().toString());
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) { // Esto es lo del toast de éxito
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) { // El toast de error.
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    // El método onBackPressed() ha sido reemplazado por el OnBackPressedDispatcher en onCreate
}
