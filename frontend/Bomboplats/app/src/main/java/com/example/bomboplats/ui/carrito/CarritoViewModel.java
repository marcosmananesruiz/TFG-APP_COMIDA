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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarritoViewModel extends AndroidViewModel {
    
    private final MutableLiveData<List<StagedBombo>> itemsCarrito = new MutableLiveData<>(new ArrayList<>());
    private final LoginRepository loginRepository;
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CarritoViewModel(@NonNull Application application) {
        super(application);
        loginRepository = LoginRepository.getInstance(new LoginDataSource(application));
        
        executorService.execute(() -> {
            LoggedInUser user = loginRepository.getUser();
            if (user == null) {
                SharedPreferences sharedPreferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String savedEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, null);
                if (savedEmail != null) {
                    loginRepository.loadUserSession(savedEmail);
                }
            }
            cargarDatosDesdeAPI();
        });
    }

    public LiveData<List<StagedBombo>> getItemsCarrito() {
        return itemsCarrito;
    }

    public void cargarDatosDesdeAPI() {
        LoggedInUser user = loginRepository.getUser();
        if (user != null) {
            List<StagedBombo> cartMap = user.getCartPlates();
            itemsCarrito.postValue(new ArrayList<>(cartMap));
        }
    }

    public void agregarAlCarrito(Bombo bombo, int cantidad, List<String> modificaciones) {
        List<StagedBombo> currentItems = this.itemsCarrito.getValue();
        List<StagedBombo> newList = currentItems != null ? new ArrayList<>(currentItems) : new ArrayList<>();

        boolean encontrado = false;
        for (int i = 0; i < newList.size(); i++) {
            StagedBombo sb = newList.get(i);
            if (sonMismosPlatos(sb, bombo.getId(), modificaciones)) {
                StagedBombo updated = new StagedBombo(sb.getBombo(), sb.getCantidad() + cantidad, sb.getModificaciones());
                newList.set(i, updated);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            newList.add(new StagedBombo(bombo, cantidad, modificaciones));
        }

        this.itemsCarrito.setValue(newList);
        sincronizarConAPI();
    }

    private boolean sonMismosPlatos(StagedBombo sb, String bomboId, List<String> nuevasModificaciones) {
        if (sb.getBombo() == null || !sb.getBombo().getId().equals(bomboId)) {
            return false;
        }

        List<String> modsExistentes = sb.getModificaciones() != null ? sb.getModificaciones() : new ArrayList<>();
        List<String> modsNuevas = nuevasModificaciones != null ? nuevasModificaciones : new ArrayList<>();

        if (modsExistentes.size() != modsNuevas.size()) {
            return false;
        }

        List<String> copy1 = new ArrayList<>(modsExistentes);
        List<String> copy2 = new ArrayList<>(modsNuevas);
        Collections.sort(copy1);
        Collections.sort(copy2);

        return copy1.equals(copy2);
    }

    public void removerDelCarrito(StagedBombo itemARemover) {
        List<StagedBombo> currentItems = this.itemsCarrito.getValue();
        if (currentItems == null) return;

        List<StagedBombo> newList = new ArrayList<>(currentItems);
        boolean cambiado = false;

        for (int i = 0; i < newList.size(); i++) {
            StagedBombo sb = newList.get(i);
            if (sb.getId() == itemARemover.getId()) {
                if (sb.getCantidad() > 1) {
                    sb.setCantidad(sb.getCantidad() - 1);
                } else {
                    newList.remove(i);
                }
                cambiado = true;
                break;
            }
        }

        if (cambiado) {
            this.itemsCarrito.setValue(newList);
            sincronizarConAPI();
        }
    }

    public void limpiarCarrito() {
        itemsCarrito.postValue(new ArrayList<>());
        sincronizarConAPI();
    }

    private void sincronizarConAPI() {
        final List<StagedBombo> mapaUI = itemsCarrito.getValue();
        if (mapaUI != null) {
            executorService.execute(() -> {
                loginRepository.setCartMap(new ArrayList<>(mapaUI));
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
