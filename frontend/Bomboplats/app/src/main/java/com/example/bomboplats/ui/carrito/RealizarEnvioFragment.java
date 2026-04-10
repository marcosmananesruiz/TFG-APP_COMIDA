package com.example.bomboplats.ui.carrito;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.PedidoControllerApi;
import com.example.bomboplats.api.Plato;
import com.example.bomboplats.api.User;
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import com.example.bomboplats.ui.estadobombos.EstadoBombosViewModel;
import com.example.bomboplats.ui.historial.HistorialViewModel;
import com.example.bomboplats.ui.historial.Pedido;
import com.example.bomboplats.ui.historial.PedidoItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RealizarEnvioFragment extends Fragment {

    private EditText etDireccion, etTarjetaNumero, etTarjetaExp, etTarjetaCvv;
    private RadioGroup rgMetodoPago;
    private View cardTarjetaDatos;
    private Button btnConfirmar;
    private ImageButton btnSeleccionarDireccion;
    private CarritoViewModel carritoViewModel;
    private HistorialViewModel historialViewModel;
    private EstadoBombosViewModel estadoBombosViewModel;
    private UserViewModel userViewModel;
    private FoodRepository foodRepository;
    private PedidoControllerApi pedidoApi;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realizarenvio, container, false);

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        historialViewModel = new ViewModelProvider(requireActivity()).get(HistorialViewModel.class);
        estadoBombosViewModel = new ViewModelProvider(requireActivity()).get(EstadoBombosViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        foodRepository = FoodRepository.getInstance(requireContext());
        pedidoApi = new PedidoControllerApi();

        etDireccion = view.findViewById(R.id.et_direccion);
        etTarjetaNumero = view.findViewById(R.id.et_tarjeta_numero);
        etTarjetaExp = view.findViewById(R.id.et_tarjeta_exp);
        etTarjetaCvv = view.findViewById(R.id.et_tarjeta_cvv);
        rgMetodoPago = view.findViewById(R.id.rg_metodo_pago);
        cardTarjetaDatos = view.findViewById(R.id.card_tarjeta_datos);
        btnConfirmar = view.findViewById(R.id.btn_confirmar_pedido);
        btnSeleccionarDireccion = view.findViewById(R.id.btn_seleccionar_direccion);

        rgMetodoPago.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_efectivo) {
                cardTarjetaDatos.setVisibility(View.GONE);
            } else {
                cardTarjetaDatos.setVisibility(View.VISIBLE);
            }
        });

        btnConfirmar.setOnClickListener(v -> {
            if (validarCampos()) {
                procesarPedido();
            }
        });

        btnSeleccionarDireccion.setOnClickListener(v -> mostrarDialogoDirecciones());

        return view;
    }

    private void mostrarDialogoDirecciones() {
        List<String> direcciones = userViewModel.getAddresses().getValue();
        if (direcciones == null || direcciones.isEmpty()) {
            Toast.makeText(getContext(), R.string.label_no_direcciones, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] arrayDirecciones = direcciones.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.titulo_direcciones);
        builder.setItems(arrayDirecciones, (dialog, which) -> {
            etDireccion.setText(arrayDirecciones[which]);
        });
        builder.show();
    }

    private void procesarPedido() {
        Map<String, Integer> itemsMap = carritoViewModel.getItemsCarrito().getValue();
        if (itemsMap == null || itemsMap.isEmpty()) return;

        executorService.execute(() -> {
            String userEmail = userViewModel.getEmail().getValue();
            String userId = userViewModel.getUserId().getValue();
            if (userEmail == null || userId == null) return;

            try {
                for (Map.Entry<String, Integer> entry : itemsMap.entrySet()) {
                    Bombo b = buscarBomboPorId(entry.getKey());
                    if (b != null) {
                        for (int i = 0; i < entry.getValue(); i++) {
                            com.example.bomboplats.api.Pedido apiPedido = new com.example.bomboplats.api.Pedido();
                            
                            Plato apiPlato = new Plato();
                            apiPlato.setId(b.getId());
                            apiPedido.setPlato(apiPlato);
                            
                            User apiUser = new User();
                            apiUser.setId(userId);
                            apiUser.setEmail(userEmail);
                            apiPedido.setUser(apiUser);
                            
                            apiPedido.setEstado(com.example.bomboplats.api.Pedido.EstadoEnum.PREPARING);
                            
                            pedidoApi.register2(apiPedido);
                        }
                    }
                }

                // Notificar éxito y limpiar en el hilo principal
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), R.string.toast_pedido_realizado_exito, Toast.LENGTH_LONG).show();
                        carritoViewModel.limpiarCarrito();
                        historialViewModel.getPedidos(); // Dispara refresh
                        estadoBombosViewModel.getPedidosEnEstado(); // Dispara refresh
                        
                        if (getParentFragmentManager() != null) {
                            getParentFragmentManager().popBackStack();
                        }
                    });
                }

            } catch (ApiException e) {
                Log.e("RealizarEnvio", "Error al registrar pedido: " + e.getMessage());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private Bombo buscarBomboPorId(String id) {
        return foodRepository.getBomboPorId(id);
    }

    private boolean validarCampos() {
        if (etDireccion.getText().toString().trim().isEmpty()) {
            etDireccion.setError(getString(R.string.hint_direccion));
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
