package com.example.bomboplats.ui.misbombos;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.LoginRepository;
import com.example.bomboplats.data.model.LoggedInUser;
import com.example.bomboplats.ui.general.FavoritosProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritosViewModel extends AndroidViewModel implements FavoritosProvider {
    
    private final MutableLiveData<List<String>> idsFavoritos = new MutableLiveData<>(new ArrayList<>());
    private final LoginRepository loginRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public FavoritosViewModel(@NonNull Application application) {
        super(application);
        loginRepository = LoginRepository.getInstance(new LoginDataSource(application));
        cargarFavoritos();
    }

    public LiveData<List<String>> getIdsFavoritos() {
        return idsFavoritos;
    }

    public void toggleFavorito(String restauranteId, String bomboId) {
        List<String> listaActual = idsFavoritos.getValue();
        if (listaActual == null) listaActual = new ArrayList<>();
        else listaActual = new ArrayList<>(listaActual);

        String key = restauranteId + ":" + bomboId;
        if (listaActual.contains(key)) {
            listaActual.remove(key);
        } else {
            listaActual.add(key);
        }
        idsFavoritos.setValue(listaActual);
        guardarFavoritos(listaActual);
    }

    @Override
    public boolean esFavorito(String restauranteId, String bomboId) {
        List<String> listaActual = idsFavoritos.getValue();
        String key = restauranteId + ":" + bomboId;
        return listaActual != null && listaActual.contains(key);
    }

    private void guardarFavoritos(List<String> listaPlana) {
        executorService.execute(() -> {
            Map<String, List<String>> mapParaAPI = new HashMap<>();
            for (String item : listaPlana) {
                String[] parts = item.split(":");
                if (parts.length == 2) {
                    mapParaAPI.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
                }
            }
            loginRepository.setFavoritesMap(mapParaAPI);
        });
    }

    private void cargarFavoritos() {
        executorService.execute(() -> {
            LoggedInUser user = loginRepository.getUser();
            if (user != null) {
                List<String> listaPlana = new ArrayList<>();
                Map<String, List<String>> favMap = user.getFavoritePlates();
                if (favMap != null) {
                    for (Map.Entry<String, List<String>> entry : favMap.entrySet()) {
                        for (String bomboId : entry.getValue()) {
                            listaPlana.add(entry.getKey() + ":" + bomboId);
                        }
                    }
                }
                idsFavoritos.postValue(listaPlana);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
