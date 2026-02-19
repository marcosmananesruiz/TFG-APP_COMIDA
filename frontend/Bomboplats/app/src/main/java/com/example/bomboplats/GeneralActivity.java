package com.example.bomboplats;

import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bomboplats.ui.configuracion.ConfiguracionFragment;
import com.example.bomboplats.ui.cuenta.CuentaFragment;
import com.example.bomboplats.ui.general.GeneralFragment;
import com.example.bomboplats.ui.historial.HistorialFragment;
import com.example.bomboplats.ui.misbombos.MisBombosFragment;
import com.example.bomboplats.ui.notificaciones.NotificacionesFragment;
import com.google.android.material.navigation.NavigationView;

public class GeneralActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

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

        // Cargar el fragmento inicial y seleccionar el item del menú
        if (savedInstanceState == null) {
            loadFragment(new GeneralFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
