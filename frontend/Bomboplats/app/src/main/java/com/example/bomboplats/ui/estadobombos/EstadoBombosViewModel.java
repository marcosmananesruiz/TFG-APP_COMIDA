package com.example.bomboplats.ui.estadobombos;

import android.app.Application;
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
            if (!EstadoPedido.ESTADO_ENTREGADO.equals(ep.getEstado())) return false;
        }
        return true;
    }

    public LiveData<List<EstadoPedido>> getPedidosEnEstado() {
        return repository.getPedidosEnEstado();
    }

    public void agregarPedidoAEstado(Pedido pedido) {
        List<EstadoPedido> listaActual = new ArrayList<>(repository.getListaActual());
        listaActual.add(new EstadoPedido(pedido, EstadoPedido.ESTADO_PREPARACION));
        repository.guardarEnDisco(getApplication(), listaActual);
        lanzarWorkerDeEstado();
    }

    public void simularPasoTiempo() {
        List<EstadoPedido> listaActual = new ArrayList<>(repository.getListaActual());
        boolean huboCambios = false;

        String tituloNoti = getApplication().getString(R.string.noti_titulo_estado);

        for (EstadoPedido ep : listaActual) {
            if (EstadoPedido.ESTADO_PREPARACION.equals(ep.getEstado())) {
                ep.setEstado(EstadoPedido.ESTADO_CAMINO);
                huboCambios = true;
                String mensaje = getApplication().getString(R.string.noti_msg_de_camino, String.valueOf(ep.getPedido().getId()));
                NotificationHelper.showNotification(getApplication(), tituloNoti, mensaje);
            } else if (EstadoPedido.ESTADO_CAMINO.equals(ep.getEstado())) {
                ep.setEstado(EstadoPedido.ESTADO_ENTREGADO);
                huboCambios = true;
                String mensaje = getApplication().getString(R.string.noti_msg_entregado, String.valueOf(ep.getPedido().getId()));
                NotificationHelper.showNotification(getApplication(), tituloNoti, mensaje);
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
