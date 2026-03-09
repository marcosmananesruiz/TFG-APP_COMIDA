package com.example.bomboplats.ui.carrito;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.BomboConCantidad;
import com.example.bomboplats.ui.estadobombos.EstadoBombosViewModel;
import com.example.bomboplats.ui.historial.HistorialViewModel;
import com.example.bomboplats.ui.historial.Pedido;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class RealizarEnvioFragment extends Fragment {

    private EditText etDireccion, etTarjetaNumero, etTarjetaExp, etTarjetaCvv;
    private RadioGroup rgMetodoPago;
    private View cardTarjetaDatos;
    private Button btnConfirmar;
    private CarritoViewModel carritoViewModel;
    private HistorialViewModel historialViewModel;
    private EstadoBombosViewModel estadoBombosViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realizarenvio, container, false);

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        historialViewModel = new ViewModelProvider(requireActivity()).get(HistorialViewModel.class);
        estadoBombosViewModel = new ViewModelProvider(requireActivity()).get(EstadoBombosViewModel.class);

        etDireccion = view.findViewById(R.id.et_direccion);
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

        return view;
    }

    private void procesarPedido() {
        Map<String, Integer> itemsMap = carritoViewModel.getItemsCarrito().getValue();
        if (itemsMap == null || itemsMap.isEmpty()) return;

        List<BomboConCantidad> listaItems = new ArrayList<>();
        double total = 0.0;

        for (Map.Entry<String, Integer> entry : itemsMap.entrySet()) {
            Bombo b = buscarBomboPorId(entry.getKey());
            if (b != null) {
                listaItems.add(new BomboConCantidad(b, entry.getValue()));
                String precioLimpio = b.getPrecio().replace("€", "").replace(",", ".").trim();
                total += Double.parseDouble(precioLimpio) * entry.getValue();
            }
        }

        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        String idPedido = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Pedido nuevoPedido = new Pedido(idPedido, fecha, listaItems, total, etDireccion.getText().toString().trim());
        historialViewModel.agregarPedido(nuevoPedido);
        
        // CORREGIDO: Ahora pasamos el pedido completo al sistema de seguimiento
        estadoBombosViewModel.agregarPedidoAEstado(nuevoPedido);
        
        Toast.makeText(getContext(), "¡Pedido " + idPedido + " realizado!", Toast.LENGTH_LONG).show();
        carritoViewModel.limpiarCarrito();
        
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    private Bombo buscarBomboPorId(String id) {
        List<Bombo> todos = new ArrayList<>();
        todos.add(new Bombo("pad_thai", "thai_food", "Pad Thai Classic", "Fideos de arroz con gambas.", "12.50€"));
        todos.add(new Bombo("curry_verde", "thai_food", "Green Curry", "Curry verde picante.", "13.90€"));
        todos.add(new Bombo("classic_burger", "burger_place", "Clásica con Queso", "Ternera, cheddar, lechuga.", "10.50€"));
        todos.add(new Bombo("bbq_burger", "burger_place", "BBQ Special", "Ternera, bacon.", "11.90€"));
        todos.add(new Bombo("pizza_margherita", "pizza_italiana", "Pizza Margherita", "Tomate, mozzarella fresca.", "9.50€"));
        todos.add(new Bombo("pizza_4_quesos", "pizza_italiana", "Cuatro Quesos", "Varios tipos de queso.", "11.50€"));
        for (Bombo b : todos) {
            if (b.getId().equals(id)) return b;
        }
        return null;
    }

    private boolean validarCampos() {
        if (etDireccion.getText().toString().trim().isEmpty()) {
            etDireccion.setError("Introduce una dirección");
            return false;
        }
        return true;
    }
}
