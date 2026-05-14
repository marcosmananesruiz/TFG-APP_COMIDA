package com.example.bomboplats.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.bomboplats.R;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.Pedido;
import com.example.bomboplats.api.PedidoControllerApi;
import com.example.bomboplats.data.EstadoBombosRepository;
import com.example.bomboplats.data.model.EstadoPedido;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EstadoBomboWorker extends Worker {

    // Nombre único para evitar duplicados en cola
    public static final String WORK_NAME = "EstadoBomboWorker";

    public EstadoBomboWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        EstadoBombosRepository repository = EstadoBombosRepository.getInstance();
        
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

            if (ahora >= ep.getTimestampEntrega()) {
                String titulo = context.getString(R.string.noti_titulo_estado);
                String msg = context.getString(R.string.noti_msg_entregado, ep.getPedido().getId());
                NotificationHelper.showNotification(context, titulo, msg);
                ep.setEstado(EstadoPedido.ESTADO_ENTREGADO);
                guardarEstado(ep);
                iterator.remove();
                huboCambios = true;
                continue;
            }

            if (transcurrido >= 180000 && EstadoPedido.ESTADO_PREPARACION.equals(ep.getEstado())) {
                ep.setEstado(EstadoPedido.ESTADO_CAMINO);
                guardarEstado(ep);
            }

            if (!estadoAnterior.equals(ep.getEstado())) {
                huboCambios = true;
                String titulo = context.getString(R.string.noti_titulo_estado);
                String msg = "";
                if (EstadoPedido.ESTADO_CAMINO.equals(ep.getEstado())) {
                    msg = context.getString(R.string.noti_msg_de_camino, ep.getPedido().getId());
                }
                if (!msg.isEmpty()) {
                    NotificationHelper.showNotification(context, titulo, msg);
                }
            }

            pendientes = true;
        }

        if (huboCambios) {
            repository.guardarEnDisco(context, listaActual);
        }

        if (pendientes) {
            Log.d("EstadoBomboWorker", "Encolando siguiente ejecución en 30s...");
            encolarSiguiente(context);
        } else {
            Log.d("EstadoBomboWorker", "No hay pedidos pendientes, deteniendo ciclo.");
        }

        return Result.success();
    }

    public static void encolarSiguiente(Context context) {
        OneTimeWorkRequest nextWork = new OneTimeWorkRequest.Builder(EstadoBomboWorker.class)
                .setInitialDelay(30, TimeUnit.SECONDS)
                .build();
        // Sin unique work — evita conflictos al encolarse desde dentro del propio worker
        WorkManager.getInstance(context).enqueue(nextWork);
    }

    private void guardarEstado(EstadoPedido ep) {
        Executors.newSingleThreadExecutor().execute(() -> {
            PedidoControllerApi pedidoApi = new PedidoControllerApi();
            try {
                Pedido pedido = pedidoApi.getPedidoById(ep.getPedido().getId());
                pedido.setEstado(convertEstado(ep.getEstado()));
                boolean success = pedidoApi.updatePedido(pedido);
                if (success) {
                    Log.i("EstadoBomboWorker", "Estado del pedido actualizado correctamente");
                } else {
                    Log.e("EstadoBomboWorker", "Error actualizando el estado del pedido");
                }
            } catch (ApiException e) {
                Log.e("EstadoBomboWorker", e.getMessage());
            }
        });
    }

    private Pedido.EstadoEnum convertEstado(String estado) {
        return switch (estado) {
            case EstadoPedido.ESTADO_CAMINO -> Pedido.EstadoEnum.DELIVERING;
            case EstadoPedido.ESTADO_ENTREGADO -> Pedido.EstadoEnum.DELIVERED;
            default -> Pedido.EstadoEnum.PREPARING;
        };
    }
}