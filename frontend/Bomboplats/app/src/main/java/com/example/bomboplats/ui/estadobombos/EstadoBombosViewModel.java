package com.example.bomboplats.ui.estadobombos;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.example.bomboplats.R;
import com.example.bomboplats.data.EstadoBombosRepository;
import com.example.bomboplats.data.model.EstadoPedido;
import com.example.bomboplats.ui.historial.Pedido;
import com.example.bomboplats.utils.EstadoBomboWorker;
import com.example.bomboplats.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ViewModel para el fragmento de pedidos en estado.
 */
public class EstadoBombosViewModel extends AndroidViewModel {
    private final EstadoBombosRepository repository = EstadoBombosRepository.getInstance();
    private final WorkManager workManager;

    public EstadoBombosViewModel(@NonNull Application application) {
        super(application);
        workManager = WorkManager.getInstance(application);
        
        cargarPedidos();
        
        if (!pedidosTodosEntregados()) {
            lanzarWorkerDeEstado();
        }
    }

    public void cargarPedidos() {
        repository.cargarDesdeDisco(getApplication());
    }

    private boolean pedidosTodosEntregados() {
        List<EstadoPedido> listaActual = repository.getListaActual();
        if (listaActual.isEmpty()) return true;
        for (EstadoPedido ep : listaActual) {
            if (!EstadoPedido.ESTADO_ENTREGADO.equals(ep.getEstado())) return false;
        }
        return true;
    }

    public List<EstadoPedido> getPedidosEnEstado(Context context) {

        List<EstadoPedido> list = this.repository.getPedidosEnEstado();
        if (list != null && list.isEmpty()) {
            this.repository.cargarDesdeDisco(context);
        }

        return repository.getPedidosEnEstado();
    }

    // Añade un estado al pedido
    public void agregarPedidoAEstado(Pedido pedido) {
        List<EstadoPedido> listaActual = new ArrayList<>(repository.getListaActual());
        listaActual.add(new EstadoPedido(pedido, EstadoPedido.ESTADO_PREPARACION));
        repository.guardarEnDisco(getApplication(), listaActual);
        lanzarWorkerDeEstado();
    }

    //Lanza el Worker que se encarga de actualizar los estados en segundo plano.
    public void lanzarWorkerDeEstado() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(EstadoBomboWorker.class)
                .setInitialDelay(15, TimeUnit.SECONDS)
                .build();
        
        workManager.enqueueUniqueWork(
                "SeguimientoBombos",
                ExistingWorkPolicy.REPLACE, // Usamos REPLACE para asegurarnos de que se reinicie con el nuevo pedido
                workRequest
        );
    }

    // Implementación de filtrar busqueda si fuera necesario
    public void filtrar(String query) {
    }
}
