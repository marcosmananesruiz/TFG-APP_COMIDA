package com.example.bomboplats.ui.estadobombos;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.bomboplats.data.model.BomboConCantidad;
import com.example.bomboplats.data.model.EstadoBombo;

import java.util.ArrayList;
import java.util.List;

public class EstadoBombosViewModel extends ViewModel {
    private final MutableLiveData<List<EstadoBombo>> bombosEnEstado = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> eventoNotificacion = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            actualizarEstados();
            handler.postDelayed(this, 15000); // Repetir cada 15 segundos
        }
    };

    public LiveData<List<EstadoBombo>> getBombosEnEstado() {
        return bombosEnEstado;
    }

    public LiveData<String> getEventoNotificacion() {
        return eventoNotificacion;
    }

    public void agregarBombosDesdePedido(List<BomboConCantidad> bombosDelPedido) {
        List<EstadoBombo> listaActual = bombosEnEstado.getValue();
        if (listaActual != null) {
            for (BomboConCantidad b : bombosDelPedido) {
                listaActual.add(new EstadoBombo(b, "Preparando"));
            }
            bombosEnEstado.setValue(new ArrayList<>(listaActual));

            if (!bombosDelPedido.isEmpty()) {
                iniciarCicloDeActualizacion();
            }
        }
    }

    private void iniciarCicloDeActualizacion() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 15000);
    }

    private void actualizarEstados() {
        List<EstadoBombo> listaActual = bombosEnEstado.getValue();
        boolean huboCambios = false;

        if (listaActual != null) {
            for (EstadoBombo eb : listaActual) {
                String nombreBombo = eb.getBomboConCantidad().getBombo().getNombre();
                if (eb.getEstado().equals("Preparando")) {
                    eb.setEstado("De camino");
                    eventoNotificacion.setValue("Tu bombo (" + nombreBombo + ") está de camino 🛵");
                    huboCambios = true;
                } else if (eb.getEstado().equals("De camino")) {
                    eb.setEstado("Entregado");
                    eventoNotificacion.setValue("¡Tu bombo (" + nombreBombo + ") ha sido entregado! 😋");
                    huboCambios = true;
                }
            }

            if (huboCambios) {
                bombosEnEstado.setValue(new ArrayList<>(listaActual));
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(runnable);
    }
}
