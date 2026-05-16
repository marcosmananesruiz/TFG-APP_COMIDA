package com.example.bomboplats.ui.historial;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.bomboplats.data.HistorialRepository;
import java.util.List;

/**
 * ViewModel para el historial de pedidos.
 */
public class HistorialViewModel extends AndroidViewModel {
    private final HistorialRepository repository;

    // Constructor que recibe el repositorio
    public HistorialViewModel(@NonNull Application application) {
        super(application);
        this.repository = HistorialRepository.getInstance();
        // Cargamos únicamente del repositorio local (simulación)
        repository.cargarDesdeDisco(application);
    }

    public LiveData<List<Pedido>> getPedidos() {
        return repository.getPedidos();
    }

    public void refreshHistorial() {
        // Refrescamos del disco local
        repository.cargarDesdeDisco(getApplication());
    }
}
