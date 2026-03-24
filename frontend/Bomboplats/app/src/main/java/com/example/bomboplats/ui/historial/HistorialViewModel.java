package com.example.bomboplats.ui.historial;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.LoginRepository;
import com.example.bomboplats.data.model.LoggedInUser;
import java.util.ArrayList;
import java.util.List;

public class HistorialViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Pedido>> pedidos = new MutableLiveData<>(new ArrayList<>());
    private final LoginRepository loginRepository;

    public HistorialViewModel(@NonNull Application application) {
        super(application);
        // Usamos el repositorio existente que ya maneja al usuario logueado
        this.loginRepository = LoginRepository.getInstance(new LoginDataSource(application));
        cargarHistorial();
    }

    public LiveData<List<Pedido>> getPedidos() {
        return pedidos;
    }

    public void agregarPedido(Pedido pedido) {
        LoggedInUser user = loginRepository.getUser();
        if (user != null) {
            List<Pedido> historial = user.getOrderHistory();
            historial.add(0, pedido);
            loginRepository.saveUser();
            pedidos.setValue(new ArrayList<>(historial));
        }
    }

    private void cargarHistorial() {
        LoggedInUser user = loginRepository.getUser();
        if (user != null) {
            pedidos.setValue(new ArrayList<>(user.getOrderHistory()));
        } else {
            pedidos.setValue(new ArrayList<>());
        }
    }
}
