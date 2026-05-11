package com.example.bomboplats.ui.carrito;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.LoginRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.LoggedInUser;
import com.example.bomboplats.data.model.StagedBombo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarritoViewModel extends AndroidViewModel {
    
    // Clave: "restauranteId:bomboId", Valor: cantidad
    private final MutableLiveData<List<StagedBombo>> itemsCarrito = new MutableLiveData<>(new ArrayList<>());
    private final LoginRepository loginRepository;
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CarritoViewModel(@NonNull Application application) {
        super(application);
        loginRepository = LoginRepository.getInstance(new LoginDataSource(application));
        
        // Realizar la carga inicial en un hilo secundario para evitar NetworkOnMainThreadException
        executorService.execute(() -> {
            // Intentar restaurar sesión si no hay usuario en el repositorio
            LoggedInUser user = loginRepository.getUser();
            if (user == null) {
                SharedPreferences sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String savedEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, null);
                if (savedEmail != null) {
                    loginRepository.loadUserSession(savedEmail);
                }
            }
            
            // Cargar datos (que ahora vienen de la API a través del usuario)
            cargarDatosDesdeAPI();
        });
    }

    public LiveData<List<StagedBombo>> getItemsCarrito() {
        return itemsCarrito;
    }

    private void cargarDatosDesdeAPI() {
        LoggedInUser user = loginRepository.getUser();
        if (user != null) {
            // Nueva estructura: Map<RestauranteId, List<BomboId>>
            List<StagedBombo> cartMap = user.getCartPlates();
            itemsCarrito.postValue(cartMap);
        }
    }

    public void agregarAlCarrito(Bombo bombo, int cantidad, List<String> modificaciones) {

        if (this.itemsCarrito.getValue() == null) {
            this.itemsCarrito.postValue(new ArrayList<>());
        }

        List<StagedBombo> stagedBombos = this.itemsCarrito.getValue();
        stagedBombos.add(new StagedBombo(bombo, cantidad, modificaciones));
        this.itemsCarrito.postValue(stagedBombos);

        sincronizarConAPI();
    }

    public void removerDelCarrito(StagedBombo bombo) {
        if (this.itemsCarrito.getValue() == null) {
            this.itemsCarrito.postValue(new ArrayList<>());
        } else {
            List<StagedBombo> stagedBombos = this.itemsCarrito.getValue();
            stagedBombos.remove(bombo);
        }
        sincronizarConAPI();
    }

    public void limpiarCarrito() {
        itemsCarrito.setValue(new ArrayList<>());
        sincronizarConAPI();
    }

    private void sincronizarConAPI() {
        final List<StagedBombo> mapaUI = itemsCarrito.getValue();
        if (mapaUI != null) {
            executorService.execute(() -> {
                // Convertir el mapa plano "restauranteId:bomboId" al mapa jerárquico
                loginRepository.setCartMap(mapaUI);
            });
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }


    public StagedBombo findStagedBomboById(int id) {
        List<StagedBombo> stagedBombos = this.itemsCarrito.getValue();
        if (stagedBombos == null) return null;
        return stagedBombos.stream().filter(stagedBombo -> stagedBombo.getId() == id).findFirst().orElse(null);
    }
}
