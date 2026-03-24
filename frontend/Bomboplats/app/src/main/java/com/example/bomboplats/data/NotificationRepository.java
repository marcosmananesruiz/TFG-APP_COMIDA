package com.example.bomboplats.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationRepository {
    private static NotificationRepository instance;
    private final MutableLiveData<List<String>> notifications = new MutableLiveData<>(new ArrayList<>());
    
    private static final String PREF_NAME = "bomboplats_notis_prefs";
    private static final String KEY_NOTIS_BASE = "lista_notificaciones";
    private static final String USER_PREFS = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";

    private NotificationRepository() {}

    public static synchronized NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    public LiveData<List<String>> getNotifications() {
        return notifications;
    }

    private String getActiveEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CURRENT_USER_EMAIL, null);
    }

    private String getUserKey(Context context) {
        String email = getActiveEmail(context);
        return (email != null) ? KEY_NOTIS_BASE + "_" + email : null;
    }

    /**
     * Carga las notificaciones del disco y actualiza el LiveData.
     * Devuelve la lista cargada para uso síncrono.
     */
    public List<String> cargarDesdeDisco(Context context) {
        String key = getUserKey(context);
        List<String> lista = new ArrayList<>();
        if (key == null) {
            notifications.postValue(lista);
            return lista;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(key, null);
        
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    lista.add(array.getString(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifications.postValue(lista);
        return lista;
    }

    public void addNotification(Context context, String message) {
        String key = getUserKey(context);
        if (key == null) return;

        // Intentamos obtener la lista actual del LiveData
        List<String> current = notifications.getValue();
        
        // Si el LiveData está vacío o no ha sido inicializado (común en el Worker),
        // forzamos una carga síncrona del disco para no perder datos.
        if (current == null || current.isEmpty()) {
            current = cargarDesdeDisco(context);
        }

        List<String> newList = new ArrayList<>(current);
        newList.add(0, message);
        
        // Actualizamos el LiveData (esto refrescará el Fragment si está abierto)
        notifications.postValue(newList);
        
        // Guardamos en el disco de forma persistente
        saveToDisk(context, newList, key);
    }

    public void removeNotification(Context context, int index) {
        String key = getUserKey(context);
        if (key == null) return;

        List<String> current = notifications.getValue();
        if (current != null && index >= 0 && index < current.size()) {
            List<String> newList = new ArrayList<>(current);
            newList.remove(index);
            notifications.postValue(newList);
            saveToDisk(context, newList, key);
        }
    }

    public void removeNotificationsByOrderId(Context context, String orderId) {
        String key = getUserKey(context);
        if (key == null) return;

        List<String> current = notifications.getValue();
        if (current == null || current.isEmpty()) {
            current = cargarDesdeDisco(context);
        }

        List<String> newList = new ArrayList<>(current);
        boolean removed = false;
        Iterator<String> iterator = newList.iterator();
        while (iterator.hasNext()) {
            String noti = iterator.next();
            if (noti.contains("#" + orderId)) {
                iterator.remove();
                removed = true;
            }
        }

        if (removed) {
            notifications.postValue(newList);
            saveToDisk(context, newList, key);
        }
    }

    private void saveToDisk(Context context, List<String> list, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            JSONArray array = new JSONArray(list);
            prefs.edit().putString(key, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
