package com.example.bomboplats;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bomboplats.ui.general.GeneralFragment;
import com.google.android.material.navigation.NavigationView;

public class GeneralActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavigationView navigationViewBottom;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerLayout y NavigationViews
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

        // Listener de items del menú principal
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new GeneralFragment());
            } else if (itemId == R.id.nav_settings) {
                // Aquí cargarías tu fragment "Configuración"
            } else if (itemId == R.id.nav_misbombos) {
                // ...
            }
            // Deseleccionar items del menú inferior si se selecciona algo arriba
            navigationViewBottom.getMenu().findItem(R.id.nav_cerrarSesion).setChecked(false);
            
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Listener para el menú inferior (Cerrar sesión)
        navigationViewBottom.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_cerrarSesion) {
                Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
                // Aquí iría tu lógica de logout
                finish(); 
            }
            
            // Deseleccionar items del menú superior si se selecciona algo abajo
            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
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
