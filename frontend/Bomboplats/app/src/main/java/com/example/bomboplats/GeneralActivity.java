package com.example.bomboplats;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bomboplats.ui.configuracion.ConfiguracionFragment;
import com.example.bomboplats.ui.cuenta.CuentaFragment;
import com.example.bomboplats.ui.general.BombosFragment;
import com.example.bomboplats.ui.general.GeneralFragment;
import com.example.bomboplats.ui.historial.HistorialFragment;
import com.example.bomboplats.ui.login.LoginActivity;
import com.example.bomboplats.ui.misbombos.MisBombosFragment;
import com.example.bomboplats.ui.notificaciones.NotificacionesFragment;
import com.google.android.material.navigation.NavigationView;

public class GeneralActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavigationView navigationViewBottom;
    private Toolbar toolbar;
    private long backPressedTime;
    private Toast backToast;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Botón del carrito en la Toolbar
        ImageView cartButton = findViewById(R.id.toolbar_shopping_cart);
        cartButton.setOnClickListener(v -> loadFragment(new MisBombosFragment()));

        // Barra de búsqueda reactiva
        searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                String query = s.toString();
                
                if (currentFragment instanceof GeneralFragment) {
                    ((GeneralFragment) currentFragment).filtrar(query);
                } else if (currentFragment instanceof BombosFragment) {
                    ((BombosFragment) currentFragment).filtrar(query);
                } else if (currentFragment instanceof MisBombosFragment) {
                    ((MisBombosFragment) currentFragment).filtrar(query);
                } else if (currentFragment instanceof CuentaFragment) {
                    ((CuentaFragment) currentFragment).filtrar(query);
                } else if (currentFragment instanceof HistorialFragment) {
                    ((HistorialFragment) currentFragment).filtrar(query);
                } else if (currentFragment instanceof NotificacionesFragment) {
                    ((NotificacionesFragment) currentFragment).filtrar(query);
                } else if (currentFragment instanceof ConfiguracionFragment) {
                    ((ConfiguracionFragment) currentFragment).filtrar(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ImageView searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(v -> {
            String searchText = searchEditText.getText().toString();
            Toast.makeText(GeneralActivity.this, "Buscando: " + searchText, Toast.LENGTH_SHORT).show();
        });

        // DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationViewBottom = findViewById(R.id.navigation_view_bottom);

        // Toggle (icono de hamburguesa)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Listener de items del menú lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new GeneralFragment());
            } else if (itemId == R.id.nav_misbombos) {
                loadFragment(new MisBombosFragment());
            } else if (itemId == R.id.nav_settings) {
                loadFragment(new CuentaFragment());
            } else if (itemId == R.id.nav_historialDeBombos) {
                loadFragment(new HistorialFragment());
            } else if (itemId == R.id.nav_notificaciones) {
                loadFragment(new NotificacionesFragment());
            } else if (itemId == R.id.nav_configuracion) {
                loadFragment(new ConfiguracionFragment());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Listener para el menú inferior (Cerrar Sesión)
        navigationViewBottom.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_cerrarSesion) {
                Intent intent = new Intent(GeneralActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            return true;
        });

        // Cargar el fragmento inicial
        if (savedInstanceState == null) {
            loadFragment(new GeneralFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Manejar el botón de "atrás"
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else if (!(currentFragment instanceof GeneralFragment)) {
                    loadFragment(new GeneralFragment());
                    navigationView.setCheckedItem(R.id.nav_home);
                } else if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    if (backToast != null) {
                        backToast.cancel();
                    }
                    finishAffinity();
                } else {
                    backToast = Toast.makeText(GeneralActivity.this, "Pulsa otra vez para salir", Toast.LENGTH_SHORT);
                    backToast.show();
                    backPressedTime = System.currentTimeMillis();
                }
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Añadimos las animaciones para que se deslicen al entrar y salir
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    // Actualizamos el método de navegación desde GeneralFragment para incluir animaciones también
    public void onRestauranteClickFromFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
