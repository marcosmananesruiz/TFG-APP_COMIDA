package com.example.bomboplats.ui.estadobombos;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.example.bomboplats.data.EstadoBombosRepository;
import com.example.bomboplats.data.model.EstadoPedido;
import com.example.bomboplats.ui.historial.Pedido;
import com.example.bomboplats.utils.EstadoBomboWorker;
import com.example.bomboplats.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EstadoBombosViewModel extends AndroidViewModel {
    private final EstadoBombosRepository repository = EstadoBombosRepository.getInstance();
    private final WorkManager workManager;

    public EstadoBombosViewModel(@NonNull Application application) {
        super(application);
        workManager = WorkManager.getInstance(application);
        
        if (!pedidosTodosEntregados()) {
            lanzarWorkerDeEstado();
        }
    }

    private boolean pedidosTodosEntregados() {
        List<EstadoPedido> listaActual = repository.getListaActual();
        if (listaActual.isEmpty()) return true;
        for (EstadoPedido ep : listaActual) {
            if (!"Entregado".equals(ep.getEstado())) return false;
        }
        return true;
    }

    public LiveData<List<EstadoPedido>> getPedidosEnEstado() {
        return repository.getPedidosEnEstado();
    }

    public void agregarPedidoAEstado(Pedido pedido) {
        List<EstadoPedido> listaActual = new ArrayList<>(repository.getListaActual());
        listaActual.add(new EstadoPedido(pedido, "Preparando"));
        repository.guardarEnDisco(getApplication(), listaActual);
        lanzarWorkerDeEstado();
    }

    public void simularPasoTiempo() {
        List<EstadoPedido> listaActual = new ArrayList<>(repository.getListaActual());
        boolean huboCambios = false;

        for (EstadoPedido ep : listaActual) {
            if ("Preparando".equals(ep.getEstado())) {
                ep.setEstado("De camino");
                huboCambios = true;
                NotificationHelper.showNotification(getApplication(), "Simulación: Estado de tus Bombos", "Tu pedido #" + ep.getPedido().getId() + " está de camino 🛵");
            } else if ("De camino".equals(ep.getEstado())) {
                ep.setEstado("Entregado");
                huboCambios = true;
                NotificationHelper.showNotification(getApplication(), "Simulación: Estado de tus Bombos", "¡Tu pedido #" + ep.getPedido().getId() + " ha sido entregado! 😋");
            }
        }

        if (huboCambios) {
            repository.guardarEnDisco(getApplication(), listaActual);
        }
    }

    private void lanzarWorkerDeEstado() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(EstadoBomboWorker.class)
                .setInitialDelay(15, TimeUnit.SECONDS)
                .build();
        
        workManager.enqueueUniqueWork(
                "SeguimientoBombos",
                ExistingWorkPolicy.KEEP,
                workRequest
        );
    }
}
