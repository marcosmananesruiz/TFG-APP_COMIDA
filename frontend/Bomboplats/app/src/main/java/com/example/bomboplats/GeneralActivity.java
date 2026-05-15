package com.example.bomboplats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.bomboplats.data.EstadoBombosRepository;
import com.example.bomboplats.data.NotificationRepository;
import com.example.bomboplats.data.model.StagedBombo;
import com.example.bomboplats.ui.carrito.CarritoFragment;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.carrito.RealizarEnvioFragment;
import com.example.bomboplats.ui.configuracion.ConfiguracionFragment;
import com.example.bomboplats.ui.cuenta.CuentaFragment;
import com.example.bomboplats.ui.estadobombos.EstadoBombosFragment;
import com.example.bomboplats.ui.estadobombos.EstadoBombosViewModel;
import com.example.bomboplats.ui.general.BombosFragment;
import com.example.bomboplats.ui.general.DetalleBomboFragment;
import com.example.bomboplats.ui.general.GeneralFragment;
import com.example.bomboplats.ui.historial.HistorialFragment;
import com.example.bomboplats.ui.login.LoginActivity;
import com.example.bomboplats.ui.misbombos.MisBombosFragment;
import com.example.bomboplats.ui.notificaciones.NotificacionesFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class GeneralActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavigationView navigationViewBottom;
    private Toolbar toolbar;
    private long backPressedTime;
    private Toast backToast;
    private LinearLayout searchContainer;
    private EditText searchEditText;
    private ImageView searchIcon;
    private ImageView cartButton;
    private CarritoViewModel carritoViewModel;
    private EstadoBombosViewModel estadoBombosViewModel;
    
    private CardView bannerNoInternet;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Habilitar el diseño edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_general);

        EstadoBombosRepository.getInstance().cargarDesdeDisco(getApplicationContext());
        NotificationRepository.getInstance().cargarDesdeDisco(getApplicationContext());

        carritoViewModel = new ViewModelProvider(this).get(CarritoViewModel.class);
        estadoBombosViewModel = new ViewModelProvider(this).get(EstadoBombosViewModel.class);

        bannerNoInternet = findViewById(R.id.banner_no_internet);
        ImageView btnCloseBanner = findViewById(R.id.btn_close_banner);
        if (btnCloseBanner != null) {
            btnCloseBanner.setOnClickListener(v -> bannerNoInternet.setVisibility(View.GONE));
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupUI();
        setupNavigation();
        setupNetworkMonitoring();

        if (savedInstanceState == null) {
            if (getIntent().getBooleanExtra("ir_a_estados", false)) {
                loadFragment(new EstadoBombosFragment());
                navigationView.setCheckedItem(R.id.nav_estadobombos);
            } else {
                loadFragment(new GeneralFragment());
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }

    }

    private void setupNetworkMonitoring() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;

        // Comprobación inicial
        if (!isNetworkAvailable(connectivityManager)) {
            bannerNoInternet.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(Network network) {
                    runOnUiThread(() -> bannerNoInternet.setVisibility(View.VISIBLE));
                }

                @Override
                public void onAvailable(Network network) {
                    runOnUiThread(() -> bannerNoInternet.setVisibility(View.GONE));
                }
            };
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        }
    }

    private boolean isNetworkAvailable(ConnectivityManager cm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = cm.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = cm.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || 
                                     actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            NetworkInfo nwInfo = cm.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    private void setupUI() {
        View mainContent = findViewById(R.id.main_content_container);
        View drawerContent = findViewById(R.id.drawer_content_container);

        ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, insets.top, 0, insets.bottom);
            return windowInsets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(drawerContent, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, insets.top, 0, insets.bottom);
            return windowInsets;
        });

        cartButton = findViewById(R.id.toolbar_shopping_cart);
        cartButton.setOnClickListener(v -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (currentFragment instanceof CarritoFragment fragment) {
                if (fragment.getCarrito() == null || fragment.getCarrito().isEmpty()) {
                    Toast.makeText(this, getString(R.string.carrito_vacio), Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_vaciar_carrito_titulo)
                            .setMessage(R.string.dialog_vaciar_carrito_mensaje)
                            .setPositiveButton(R.string.si, (dialog, which) -> {
                                carritoViewModel.limpiarCarrito();
                                Toast.makeText(this, getString(R.string.toast_carrito_vaciado), Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
            } else if (currentFragment instanceof RealizarEnvioFragment) {
                getSupportFragmentManager().popBackStack();
            } else {
                loadFragment(new CarritoFragment());
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateCartIconBasedOnFragment);

        searchEditText = findViewById(R.id.search_edit_text);
        searchIcon = findViewById(R.id.search_icon);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) searchIcon.setImageResource(R.drawable.ic_close);
                else searchIcon.setImageResource(R.drawable.ic_search);

                String query = s.toString();

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if (currentFragment instanceof GeneralFragment) ((GeneralFragment) currentFragment).filtrar(query);
                else if (currentFragment instanceof BombosFragment) ((BombosFragment) currentFragment).filtrar(query);
                else if (currentFragment instanceof MisBombosFragment) ((MisBombosFragment) currentFragment).filtrar(query);
                else if (currentFragment instanceof CarritoFragment) ((CarritoFragment) currentFragment).filtrar(query);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchIcon.setOnClickListener(v -> {
            if (searchEditText.getText().length() > 0) searchEditText.setText("");
            else searchEditText.requestFocus();
        });

        this.searchContainer = findViewById(R.id.search_bar_container);

        this.carritoViewModel.getItemsCarrito().observe(this, carrito -> {

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (currentFragment instanceof CarritoFragment) return;

            if (carrito == null || carrito.isEmpty()) {
                cartButton.setImageResource(R.drawable.ic_shopping_cart);
            } else {
                cartButton.setImageResource(R.drawable.ic_filled_cart);
            }
        });

    }

    private void setupNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationViewBottom = findViewById(R.id.navigation_view_bottom);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) loadFragment(new GeneralFragment());
            else if (itemId == R.id.nav_misbombos) loadFragment(new MisBombosFragment());
            else if (itemId == R.id.nav_estadobombos) loadFragment(new EstadoBombosFragment());
            else if (itemId == R.id.nav_settings) loadFragment(new CuentaFragment());
            else if (itemId == R.id.nav_historialDeBombos) loadFragment(new HistorialFragment());
            else if (itemId == R.id.nav_notificaciones) loadFragment(new NotificacionesFragment());
            else if (itemId == R.id.nav_configuracion) loadFragment(new ConfiguracionFragment());
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        navigationViewBottom.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_cerrarSesion) {
                SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                prefs.edit().remove("current_user_email").remove("session_expiration").apply();
                Intent intent = new Intent(GeneralActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
                else if (getSupportFragmentManager().getBackStackEntryCount() > 0) getSupportFragmentManager().popBackStack();
                else if (!(currentFragment instanceof GeneralFragment)) {
                    loadFragment(new GeneralFragment());
                    navigationView.setCheckedItem(R.id.nav_home);
                } else if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    if (backToast != null) backToast.cancel();
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

        if (fragment instanceof CuentaFragment
                || fragment instanceof ConfiguracionFragment
                || fragment instanceof NotificacionesFragment
                || fragment instanceof HistorialFragment
                || fragment instanceof EstadoBombosFragment
                || fragment instanceof DetalleBomboFragment) {
            searchContainer.setVisibility(View.INVISIBLE);
        } else {
            searchContainer.setVisibility(View.VISIBLE);
        }
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
        if (fragment instanceof CarritoFragment) {
            cartButton.setImageResource(R.drawable.ic_close);
        } else {
            List<StagedBombo> carrito = this.carritoViewModel.getItemsCarrito().getValue();
            if (carrito == null || carrito.isEmpty()) {
                cartButton.setImageResource(R.drawable.ic_shopping_cart);
            } else {
                cartButton.setImageResource(R.drawable.ic_filled_cart);
            }
        }
    }

    private void updateCartIconBasedOnFragment() {
        getWindow().getDecorView().post(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            updateCartIcon(currentFragment);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkCallback != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}
