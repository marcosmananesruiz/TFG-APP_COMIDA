package com.example.bomboplats.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;

/**
 * BroadcastReceiver para el estado de los bombos.
 */
public class EstadoBomboReceiver extends BroadcastReceiver {

    public static final String ACTION_CHECK = "com.example.bomboplats.CHECK_ESTADO";
    private static final int INTERVALO_MS = 30_000; // 30 segundos

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_CHECK.equals(action) || Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            // Lanzar el Worker
            OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(EstadoBomboWorker.class)
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build();
            WorkManager.getInstance(context).enqueue(work);

            // Programar la siguiente comprobación
            programarSiguiente(context);
        }
    }

    // Programar la siguiente comprobación
    public static void programarSiguiente(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, EstadoBomboReceiver.class);
        intent.setAction(ACTION_CHECK);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long proximaEjecucion = System.currentTimeMillis() + INTERVALO_MS;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, proximaEjecucion, pendingIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, proximaEjecucion, pendingIntent);
        }
    }

    // Cancelar la programación de la comprobación para no ejecutarla cada 30s
    public static void cancelar(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, EstadoBomboReceiver.class);
        intent.setAction(ACTION_CHECK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
}
