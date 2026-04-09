package com.example.bomboplats.ui.historial;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.PedidoControllerApi;
import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.LoginRepository;
import com.example.bomboplats.data.model.LoggedInUser;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistorialViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Pedido>> pedidos = new MutableLiveData<>(new ArrayList<>());
    private final LoginRepository loginRepository;
    private final PedidoControllerApi pedidoApi;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public HistorialViewModel(@NonNull Application application) {
        super(application);
        this.loginRepository = LoginRepository.getInstance(new LoginDataSource(application));
        this.pedidoApi = new PedidoControllerApi();
        cargarHistorial();
    }

    public LiveData<List<Pedido>> getPedidos() {
        return pedidos;
    }

    public void agregarPedido(Pedido pedido) {
        // En la nueva arquitectura, los pedidos se registran a través de la API
        // al finalizar la compra. Aquí refrescamos la lista.
        cargarHistorial();
    }

    private void cargarHistorial() {
        executorService.execute(() -> {
            LoggedInUser user = loginRepository.getUser();
            if (user != null) {
                try {
                    List<com.example.bomboplats.api.Pedido> apiPedidos = pedidoApi.getPedidoByUser(user.getEmail());
                    List<Pedido> localHistorial = new ArrayList<>();
                    if (apiPedidos != null) {
                        for (com.example.bomboplats.api.Pedido ap : apiPedidos) {
                            localHistorial.add(convertToLocalPedido(ap));
                        }
                    }
                    pedidos.postValue(localHistorial);
                } catch (ApiException e) {
                    Log.e("HistorialViewModel", "Error cargando historial: " + e.getMessage());
                }
            }
        });
    }

    private Pedido convertToLocalPedido(com.example.bomboplats.api.Pedido ap) {
        // Convertimos el modelo de la API al modelo de UI del historial
        List<PedidoItem> items = new ArrayList<>();
        double total = 0.0;
        if (ap.getPlato() != null) {
            total = ap.getPlato().getPrecio() != null ? ap.getPlato().getPrecio().doubleValue() : 0.0;
            items.add(new PedidoItem("RESTAURANTE_ID", ap.getPlato().getId(), 1));
        }

        String fecha = (ap.getEntrega() != null) ? ap.getEntrega().toString() : "N/A";

        return new Pedido(
                ap.getId(),
                fecha,
                items,
                total,
                "Dirección no disponible"
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
