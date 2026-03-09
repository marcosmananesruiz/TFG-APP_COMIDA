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
    private static final String KEY_NOTIS = "lista_notificaciones";

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

    public void cargarDesdeDisco(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_NOTIS, null);
        if (json != null) {
            try {
                List<String> lista = new ArrayList<>();
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    lista.add(array.getString(i));
                }
                notifications.postValue(lista);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addNotification(Context context, String message) {
        List<String> current = notifications.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(0, message);
        notifications.postValue(new ArrayList<>(current));
        saveToDisk(context, current);
    }

    public void removeNotification(Context context, int index) {
        List<String> current = notifications.getValue();
        if (current != null && index >= 0 && index < current.size()) {
            current.remove(index);
            notifications.postValue(new ArrayList<>(current));
            saveToDisk(context, current);
        }
    }

    public void removeNotificationsByOrderId(Context context, String orderId) {
        List<String> current = notifications.getValue();
        if (current == null) return;

        boolean removed = false;
        Iterator<String> iterator = current.iterator();
        while (iterator.hasNext()) {
            String noti = iterator.next();
            if (noti.contains("#" + orderId)) {
                iterator.remove();
                removed = true;
            }
        }

        if (removed) {
            notifications.postValue(new ArrayList<>(current));
            saveToDisk(context, current);
        }
    }

    private void saveToDisk(Context context, List<String> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            JSONArray array = new JSONArray(list);
            prefs.edit().putString(KEY_NOTIS, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
