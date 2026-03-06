package com.example.bomboplats.ui.notificaciones;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesViewModel extends ViewModel {
    private final MutableLiveData<List<String>> listaNotificaciones = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<String>> getListaNotificaciones() {
        return listaNotificaciones;
    }

    public void agregarNotificacion(String mensaje) {
        List<String> actual = listaNotificaciones.getValue();
        if (actual != null) {
            actual.add(0, mensaje); // Añadir al principio (lo más nuevo arriba)
            listaNotificaciones.setValue(new ArrayList<>(actual));
        }
    }
}
