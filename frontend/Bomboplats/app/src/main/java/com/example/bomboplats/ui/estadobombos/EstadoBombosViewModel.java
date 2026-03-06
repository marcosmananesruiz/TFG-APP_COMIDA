package com.example.bomboplats.ui.estadobombos;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.BomboConCantidad;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EstadoBombosViewModel extends AndroidViewModel {
    private static final String CHANNEL_ID = "bombo_status_channel";
    private final MutableLiveData<List<BomboEnEstado>> bombosEnEstado = new MutableLiveData<>(new ArrayList<>());
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            actualizarEstados();
            handler.postDelayed(this, 10000); // Revisar cada 10 segundos
        }
    };

    public EstadoBombosViewModel(@NonNull Application application) {
        super(application);
        createNotificationChannel();
        handler.post(timerRunnable);
    }

    public LiveData<List<BomboEnEstado>> getBombosEnEstado() {
        return bombosEnEstado;
    }

    public void agregarBombosDesdePedido(List<BomboConCantidad> items) {
        List<BomboEnEstado> listaActual = bombosEnEstado.getValue();
        if (listaActual == null) listaActual = new ArrayList<>();
        
        for (BomboConCantidad item : items) {
            for (int i = 0; i < item.getCantidad(); i++) {
                BomboEnEstado nuevo = new BomboEnEstado(UUID.randomUUID().toString(), item.getBombo());
                listaActual.add(nuevo);
                enviarNotificacion(nuevo.getBombo().getNombre(), "Tu bombo está preparándose");
            }
        }
        bombosEnEstado.setValue(new ArrayList<>(listaActual));
    }

    private void actualizarEstados() {
        List<BomboEnEstado> lista = bombosEnEstado.getValue();
        if (lista == null || lista.isEmpty()) return;

        long ahora = System.currentTimeMillis();
        boolean huboCambios = false;
        Iterator<BomboEnEstado> iterator = lista.iterator();

        while (iterator.hasNext()) {
            BomboEnEstado bombo = iterator.next();
            long diferencia = ahora - bombo.getTiempoUltimoCambio();

            if (bombo.getEstado() == BomboEnEstado.Estado.PREPARACION && diferencia >= 60000) { // 1 min
                bombo.setEstado(BomboEnEstado.Estado.ENVIO);
                enviarNotificacion(bombo.getBombo().getNombre(), "Tu bombo está de camino");
                huboCambios = true;
            } else if (bombo.getEstado() == BomboEnEstado.Estado.ENVIO && diferencia >= 60000) { // 1 min más
                bombo.setEstado(BomboEnEstado.Estado.RECIBIDO);
                enviarNotificacion(bombo.getBombo().getNombre(), "TU BOMBO ha llegado");
                huboCambios = true;
            } else if (bombo.getEstado() == BomboEnEstado.Estado.RECIBIDO && diferencia >= 300000) { // 5 min
                iterator.remove();
                huboCambios = true;
            }
        }

        if (huboCambios) {
            bombosEnEstado.setValue(new ArrayList<>(lista));
        }
    }

    private void enviarNotificacion(String titulo, String mensaje) {
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplication(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_shopping_cart)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Estado de Pedidos", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getApplication().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(timerRunnable);
    }
}
