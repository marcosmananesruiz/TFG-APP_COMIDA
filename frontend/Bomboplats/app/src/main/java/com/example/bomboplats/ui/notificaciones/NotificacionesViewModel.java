package com.example.bomboplats.ui.notificaciones;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.bomboplats.data.NotificationRepository;
import java.util.List;

public class NotificacionesViewModel extends ViewModel {
    private final NotificationRepository repository = NotificationRepository.getInstance();

    public LiveData<List<String>> getListaNotificaciones() {
        return repository.getNotifications();
    }
}
