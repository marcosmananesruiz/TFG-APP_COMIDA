package com.example.bomboplats.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.bomboplats.data.EstadoBombosRepository;
import com.example.bomboplats.data.NotificationRepository;
import com.example.bomboplats.data.model.EstadoPedido;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EstadoBomboWorker extends Worker {

    public EstadoBomboWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        EstadoBombosRepository repository = EstadoBombosRepository.getInstance();
        NotificationRepository notiRepo = NotificationRepository.getInstance();
        
        repository.cargarDesdeDisco(context);
        List<EstadoPedido> listaActual = new ArrayList<>(repository.getListaActual());
        
        boolean huboCambios = false;
        boolean pendientes = false;
        long ahora = System.currentTimeMillis();

        Iterator<EstadoPedido> iterator = listaActual.iterator();
        while (iterator.hasNext()) {
            EstadoPedido ep = iterator.next();
            long transcurrido = ahora - ep.getTimestampCreacion();
            String estadoAnterior = ep.getEstado();

            // 1. Lógica de limpieza: si lleva > 30s entregado, se borra el pedido Y sus notificaciones
            if ("Entregado".equals(ep.getEstado()) && transcurrido >= 30000) {
                String orderId = ep.getPedido().getId();
                // Borramos las notificaciones relacionadas del historial persistente
                notiRepo.removeNotificationsByOrderId(context, orderId);
                iterator.remove();
                huboCambios = true;
                continue;
            }

            // 2. Lógica de evolución de estados
            if (transcurrido >= 30000) {
                ep.setEstado("Entregado");
            } else if (transcurrido >= 15000) {
                ep.setEstado("De camino");
            }

            if (!estadoAnterior.equals(ep.getEstado())) {
                huboCambios = true;
                String msg = "De camino".equals(ep.getEstado()) ? 
                    "Tu pedido #" + ep.getPedido().getId() + " está de camino 🛵" :
                    "¡Tu pedido #" + ep.getPedido().getId() + " ha sido entregado! 😋";
                NotificationHelper.showNotification(context, "Estado de tus Bombos", msg);
            }

            if (!"Entregado".equals(ep.getEstado())) pendientes = true;
        }

        if (huboCambios) {
            repository.guardarEnDisco(context, listaActual);
        }

        if (pendientes || huboCambios) {
            OneTimeWorkRequest nextWork = new OneTimeWorkRequest.Builder(EstadoBomboWorker.class)
                    .setInitialDelay(15, TimeUnit.SECONDS)
                    .build();
            WorkManager.getInstance(context).enqueue(nextWork);
        }

        return Result.success();
    }
}
