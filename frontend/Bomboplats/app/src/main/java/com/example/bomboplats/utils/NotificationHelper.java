package com.example.bomboplats.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;
import com.example.bomboplats.ui.notificaciones.NotificacionesViewModel;

public class NotificationHelper {

    private static final String CHANNEL_ID = "estado_bombos_channel";
    private static final String CHANNEL_NAME = "Estado de Bombos";

    public static void showNotification(Context context, String titulo, String mensaje) {
        createNotificationChannel(context);

        // Guardar en el historial de notificaciones
        if (context instanceof FragmentActivity) {
            NotificacionesViewModel vm = new ViewModelProvider((FragmentActivity) context).get(NotificacionesViewModel.class);
            vm.agregarNotificacion(mensaje);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_shopping_cart)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}
