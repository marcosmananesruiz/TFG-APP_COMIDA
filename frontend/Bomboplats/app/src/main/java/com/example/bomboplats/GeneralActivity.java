package com.example.bomboplats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.bomboplats.data.EstadoBombosRepository;
import com.example.bomboplats.data.NotificationRepository;
import com.example.bomboplats.ui.carrito.CarritoFragment;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.carrito.RealizarEnvioFragment;
import com.example.bomboplats.ui.configuracion.ConfiguracionFragment;
import com.example.bomboplats.ui.cuenta.CuentaFragment;
import com.example.bomboplats.ui.estadobombos.EstadoBombosFragment;
import com.example.bomboplats.ui.estadobombos.EstadoBombosViewModel;
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
    private ImageView searchIcon;
    private ImageView cartButton;
    private CarritoViewModel carritoViewModel;
    private EstadoBombosViewModel estadoBombosViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        EstadoBombosRepository.getInstance().cargarDesdeDisco(getApplicationContext());
        NotificationRepository.getInstance().cargarDesdeDisco(getApplicationContext());

        carritoViewModel = new ViewModelProvider(this).get(CarritoViewModel.class);
        estadoBombosViewModel = new ViewModelProvider(this).get(EstadoBombosViewModel.class);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cartButton = findViewById(R.id.toolbar_shopping_cart);
        cartButton.setOnClickListener(v -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (currentFragment instanceof CarritoFragment) {
                carritoViewModel.limpiarCarrito();
                Toast.makeText(this, getString(R.string.toast_carrito_vaciado), Toast.LENGTH_SHORT).show();
            } else if (currentFragment instanceof RealizarEnvioFragment) {
                // Si estamos en completar pedido, el icono es un carrito, al darle volvemos atrás al carrito (que pondrá la X)
                getSupportFragmentManager().popBackStack();
            } else {
                loadFragment(new CarritoFragment());
            }
        });

        // Escuchar cambios en la pila para actualizar el icono de forma automática y robusta
        getSupportFragmentManager().addOnBackStackChangedListener(this::updateCartIconBasedOnFragment);

        searchEditText = findViewById(R.id.search_edit_text);
        searchIcon = findViewById(R.id.search_icon);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchIcon.setImageResource(R.drawable.ic_close);
                } else {
                    searchIcon.setImageResource(R.drawable.ic_search);
                }

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
                } else if (currentFragment instanceof EstadoBombosFragment) {
                    ((EstadoBombosFragment) currentFragment).filtrar(query);
                } else if (currentFragment instanceof CarritoFragment) {
                    ((CarritoFragment) currentFragment).filtrar(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchIcon.setOnClickListener(v -> {
            if (searchEditText.getText().length() > 0) {
                searchEditText.setText("");
            } else {
                searchEditText.requestFocus();
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationViewBottom = findViewById(R.id.navigation_view_bottom);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new GeneralFragment());
            } else if (itemId == R.id.nav_misbombos) {
                loadFragment(new MisBombosFragment());
            } else if (itemId == R.id.nav_estadobombos) {
                loadFragment(new EstadoBombosFragment());
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

        navigationViewBottom.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_cerrarSesion) {
                Intent intent = new Intent(GeneralActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            return true;
        });

        if (savedInstanceState == null) {
            if (getIntent().getBooleanExtra("ir_a_estados", false)) {
                loadFragment(new EstadoBombosFragment());
                navigationView.setCheckedItem(R.id.nav_estadobombos);
            } else {
                loadFragment(new GeneralFragment());
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }

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
                    backToast = Toast.makeText(GeneralActivity.this, getString(R.string.atras_salir), Toast.LENGTH_SHORT);
                    backToast.show();
                    backPressedTime = System.currentTimeMillis();
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getBooleanExtra("ir_a_estados", false)) {
            loadFragment(new EstadoBombosFragment());
            navigationView.setCheckedItem(R.id.nav_estadobombos);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.container, fragment);
        transaction.commit();
        
        updateCartIcon(fragment);
    }

    public void onRestauranteClickFromFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        
        updateCartIcon(fragment);
    }

    public void updateCartIcon(Fragment fragment) {
        if (cartButton == null) return;
        // Solo mostramos la X si estamos en el carrito. En completar pedido (envío) o el resto, icono normal.
        if (fragment instanceof CarritoFragment) {
            cartButton.setImageResource(R.drawable.ic_close);
        } else {
            cartButton.setImageResource(R.drawable.ic_shopping_cart);
        }
    }

    private void updateCartIconBasedOnFragment() {
        // Post para asegurar que el fragmento ya ha cambiado tras el popBackStack
        getWindow().getDecorView().post(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            updateCartIcon(currentFragment);
        });
    }
}
