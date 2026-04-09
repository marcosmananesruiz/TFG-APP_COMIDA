package com.example.bomboplats.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Pedido;
import com.example.bomboplats.api.PedidoControllerApi;
import com.example.bomboplats.data.model.EstadoPedido;
import com.example.bomboplats.ui.historial.PedidoItem;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository that manages the status of active orders.
 * Migrated from local JSON to REST API.
 */
public class EstadoBombosRepository {
    private static EstadoBombosRepository instance;
    private final MutableLiveData<List<EstadoPedido>> pedidosEnEstado = new MutableLiveData<>(new ArrayList<>());
    private final PedidoControllerApi pedidoApi;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";

    private EstadoBombosRepository() {
        this.pedidoApi = new PedidoControllerApi();
    }

    public static synchronized EstadoBombosRepository getInstance() {
        if (instance == null) {
            instance = new EstadoBombosRepository();
        }
        return instance;
    }

    public LiveData<List<EstadoPedido>> getPedidosEnEstado() {
        return pedidosEnEstado;
    }

    private String getActiveEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CURRENT_USER_EMAIL, null);
    }

    /**
     * Carga los pedidos del usuario desde la API.
     */
    public void cargarDesdeDisco(Context context) {
        String email = getActiveEmail(context);
        if (email == null) return;

        executorService.execute(() -> {
            try {
                // Obtenemos los pedidos del usuario desde la API
                List<Pedido> apiPedidos = pedidoApi.getPedidoByUser(email);
                List<EstadoPedido> localLista = new ArrayList<>();
                
                if (apiPedidos != null) {
                    for (Pedido p : apiPedidos) {
                        // Solo mostramos los que no están entregados en "Estados" (o según lógica de negocio)
                        if (p.getEstado() != Pedido.EstadoEnum.DELIVERED) {
                            localLista.add(convertToEstadoPedido(p));
                        }
                    }
                }
                pedidosEnEstado.postValue(localLista);
            } catch (ApiException e) {
                Log.e("EstadoBombosRepo", "Error loading active orders: " + e.getMessage());
            }
        });
    }

    /**
     * En la nueva arquitectura, el guardado se hace por cada pedido en la API.
     * Mantenemos el método por compatibilidad si es necesario disparar un refresh.
     */
    public void guardarEnDisco(Context context, List<EstadoPedido> lista) {
        // No guardamos en disco físico, refrescamos desde la API si fuera necesario
        // o actualizamos el estado de un pedido específico si la UI lo requiere.
        pedidosEnEstado.postValue(new ArrayList<>(lista));
    }

    public List<EstadoPedido> getListaActual() {
        List<EstadoPedido> actual = pedidosEnEstado.getValue();
        return actual != null ? actual : new ArrayList<>();
    }

    private EstadoPedido convertToEstadoPedido(Pedido p) {
        // Mapeo de estados Enum a String
        String estadoStr = EstadoPedido.ESTADO_PREPARACION;
        if (p.getEstado() == Pedido.EstadoEnum.DELIVERING) {
            estadoStr = EstadoPedido.ESTADO_CAMINO;
        } else if (p.getEstado() == Pedido.EstadoEnum.DELIVERED) {
            estadoStr = EstadoPedido.ESTADO_ENTREGADO;
        }

        // Convertimos el pedido de la API al modelo de UI usado por EstadoPedido
        List<PedidoItem> items = new ArrayList<>();
        double total = 0.0;
        if (p.getPlato() != null) {
            total = (p.getPlato().getPrecio() != null) ? p.getPlato().getPrecio().doubleValue() : 0.0;
            // Usamos un placeholder para restauranteId si no viene en el Plato
            items.add(new PedidoItem("RESTAURANTE_ID", p.getPlato().getId(), 1));
        }

        String fecha = (p.getEntrega() != null) ? p.getEntrega().toString() : "N/A";

        com.example.bomboplats.ui.historial.Pedido uiPedido = new com.example.bomboplats.ui.historial.Pedido(
                p.getId(),
                fecha,
                items,
                total,
                "Dirección no disponible"
        );

        long timestamp = (p.getEntrega() != null) 
                ? p.getEntrega().toInstant().toEpochMilli() : System.currentTimeMillis();

        return new EstadoPedido(uiPedido, estadoStr, timestamp);
    }
}
