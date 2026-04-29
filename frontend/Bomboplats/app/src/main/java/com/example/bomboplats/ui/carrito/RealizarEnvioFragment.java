package com.example.bomboplats.ui.carrito;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;
import com.example.bomboplats.api.PedidoControllerApi;
import com.example.bomboplats.api.Plato;
import com.example.bomboplats.api.User;
import com.example.bomboplats.api.Direccion;
import com.example.bomboplats.data.EstadoBombosRepository;
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.HistorialRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.EstadoPedido;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import com.example.bomboplats.ui.estadobombos.EstadoBombosViewModel;
import com.example.bomboplats.ui.historial.HistorialViewModel;
import com.example.bomboplats.ui.historial.PedidoItem;
import com.example.bomboplats.utils.NotificationHelper;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RealizarEnvioFragment extends Fragment {

    private TextView tvDireccionSeleccionada;
    private Direccion direccionSeleccionada;
    private EditText etTarjetaNumero, etTarjetaExp, etTarjetaCvv;
    private RadioGroup rgMetodoPago;
    private View cardTarjetaDatos, cardDireccion;
    private Button btnConfirmar;
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

        cardDireccion = view.findViewById(R.id.card_direccion);
        tvDireccionSeleccionada = view.findViewById(R.id.tv_direccion_seleccionada);
        etTarjetaNumero = view.findViewById(R.id.et_tarjeta_numero);
        etTarjetaExp = view.findViewById(R.id.et_tarjeta_exp);
        etTarjetaCvv = view.findViewById(R.id.et_tarjeta_cvv);
        rgMetodoPago = view.findViewById(R.id.rg_metodo_pago);
        cardTarjetaDatos = view.findViewById(R.id.card_tarjeta_datos);
        btnConfirmar = view.findViewById(R.id.btn_confirmar_pedido);

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

        cardDireccion.setOnClickListener(v -> mostrarDialogoDirecciones());

        return view;
    }

    private void mostrarDialogoDirecciones() {
        java.util.List<Direccion> direccionesObj = userViewModel.getUserAddressesObjects().getValue();
        java.util.List<String> direccionesStr = userViewModel.getAddresses().getValue();
        
        if (direccionesStr == null || direccionesStr.isEmpty() || direccionesObj == null || direccionesObj.isEmpty()) {
            Toast.makeText(getContext(), R.string.label_no_direcciones, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] arrayDirecciones = direccionesStr.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.titulo_direcciones);
        builder.setItems(arrayDirecciones, (dialog, which) -> {
            direccionSeleccionada = direccionesObj.get(which);
            tvDireccionSeleccionada.setText(arrayDirecciones[which]);
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
                // 1. Guardar en Base de Datos vía API (Remoto)
                OffsetDateTime fechaEntregaApi = OffsetDateTime.now().plusMinutes(10);

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
                            if (direccionSeleccionada != null) {
                                Set<Direccion> dirs = new LinkedHashSet<>();
                                dirs.add(direccionSeleccionada);
                                apiUser.setDirecciones(dirs);
                            }
                            apiPedido.setUser(apiUser);
                            apiPedido.setEstado(com.example.bomboplats.api.Pedido.EstadoEnum.PREPARING);
                            apiPedido.setEntrega(fechaEntregaApi);

                            try {
                                pedidoApi.register2Call(apiPedido, null).execute().close();
                            } catch (Exception e) {
                                Log.e("RealizarEnvio", "Error al guardar en API remota: " + e.getMessage());
                            }
                        }
                    }
                }

                // 2. Simulación Local
                String localOrderId = String.valueOf(System.currentTimeMillis()).substring(7);
                String fechaSimulada = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                
                List<PedidoItem> localItems = new ArrayList<>();
                double total = 0;
                for (Map.Entry<String, Integer> entry : itemsMap.entrySet()) {
                    Bombo b = buscarBomboPorId(entry.getKey());
                    if (b != null) {
                        localItems.add(new PedidoItem(b.getRestauranteId(), b.getId(), entry.getValue()));
                        total += Double.parseDouble(b.getPrecio()) * entry.getValue();
                    }
                }

                com.example.bomboplats.ui.historial.Pedido uiPedido = new com.example.bomboplats.ui.historial.Pedido(
                        localOrderId, fechaSimulada, localItems, total, tvDireccionSeleccionada.getText().toString()
                );

                HistorialRepository.getInstance().guardarPedido(getContext(), uiPedido);
                
                EstadoPedido ep = new EstadoPedido(uiPedido, EstadoPedido.ESTADO_PREPARACION);
                EstadoBombosRepository.getInstance().agregarPedido(getContext(), ep);

                // MANDAR NOTIFICACIÓN INICIAL "EN PREPARACIÓN"
                if (getContext() != null) {
                    String tituloNoti = getContext().getString(R.string.noti_titulo_estado);
                    String msgNoti = getContext().getString(R.string.noti_msg_preparacion, localOrderId);
                    NotificationHelper.showNotification(getContext(), tituloNoti, msgNoti);
                }

                // 3. Finalizar en UI
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), getString(R.string.toast_pedido_realizado_exito, localOrderId), Toast.LENGTH_LONG).show();
                        carritoViewModel.limpiarCarrito();
                        historialViewModel.refreshHistorial();
                        estadoBombosViewModel.cargarPedidos();
                        estadoBombosViewModel.lanzarWorkerDeEstado();
                        
                        if (getParentFragmentManager() != null) {
                            getParentFragmentManager().popBackStack();
                        }
                    });
                }

            } catch (Exception e) {
                Log.e("RealizarEnvio", "Error fatal al tramitar compra: " + e.getMessage());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Error al tramitar el pedido", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private Bombo buscarBomboPorId(String id) {
        String actualId = id;
        if (id.contains(":")) {
            actualId = id.split(":")[1];
        }
        return foodRepository.getBomboPorId(actualId);
    }

    private boolean validarCampos() {
        if (direccionSeleccionada == null) {
            Toast.makeText(getContext(), R.string.label_busca_entrega, Toast.LENGTH_SHORT).show();
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
