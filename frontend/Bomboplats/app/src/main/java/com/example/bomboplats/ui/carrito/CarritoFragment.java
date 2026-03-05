package com.example.bomboplats.ui.carrito;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.BomboConCantidad;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CarritoFragment extends Fragment implements CarritoAdapter.OnCarritoActionListener {

    private CarritoViewModel carritoViewModel;
    private RecyclerView recyclerView;
    private CarritoAdapter adapter;
    private TextView tvVacio;
    private TextView tvTotalPrecio;
    private LinearLayout layoutTotal;
    private Button btnPagar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrito, container, false);

        recyclerView = view.findViewById(R.id.rv_carrito);
        tvVacio = view.findViewById(R.id.tv_carrito_vacio);
        btnPagar = view.findViewById(R.id.btn_pagar);
        tvTotalPrecio = view.findViewById(R.id.tv_total_precio);
        layoutTotal = view.findViewById(R.id.layout_total);

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);

        adapter = new CarritoAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        carritoViewModel.getItemsCarrito().observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                mostrarCarritoVacio(true);
            } else {
                mostrarCarritoVacio(false);
                actualizarListaVisual(items);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Notificamos a la Activity que estamos en la pantalla del carrito
        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).updateCartIcon(this);
        }
    }

    private void mostrarCarritoVacio(boolean vacio) {
        if (vacio) {
            recyclerView.setVisibility(View.GONE);
            layoutTotal.setVisibility(View.GONE);
            tvVacio.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.VISIBLE);
            tvVacio.setVisibility(View.GONE);
        }
    }

    private void actualizarListaVisual(Map<String, Integer> items) {
        List<BomboConCantidad> listaFinal = new ArrayList<>();
        double totalCompra = 0.0;

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String id = entry.getKey();
            int cantidad = entry.getValue();
            
            Bombo b = buscarBomboPorId(id);
            if (b != null) {
                listaFinal.add(new BomboConCantidad(b, cantidad));
                try {
                    String precioLimpio = b.getPrecio().replace("€", "").replace(",", ".	").trim();
                    totalCompra += Double.parseDouble(precioLimpio) * cantidad;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        adapter.actualizarLista(listaFinal);
        tvTotalPrecio.setText(String.format(Locale.getDefault(), "%.2f€", totalCompra));
    }

    @Override
    public void onRestarClick(String bomboId) {
        carritoViewModel.removerDelCarrito(bomboId);
    }

    private Bombo buscarBomboPorId(String id) {
        List<Bombo> todos = new ArrayList<>();
        todos.add(new Bombo("pad_thai", "thai_food", "Pad Thai Classic", "Fideos de arroz con gambas y cacahuetes.", "12.50€"));
        todos.add(new Bombo("curry_verde", "thai_food", "Green Curry", "Curry verde picante con leche de coco.", "13.90€"));
        todos.add(new Bombo("classic_burger", "burger_place", "Clásica con Queso", "Ternera, cheddar, lechuga y tomate.", "10.50€"));
        todos.add(new Bombo("bbq_burger", "burger_place", "BBQ Special", "Ternera, bacon y mucha salsa barbacoa.", "11.90€"));
        todos.add(new Bombo("pizza_margherita", "pizza_italiana", "Pizza Margherita", "Tomate, mozzarella fresca y albahaca.", "9.50€"));
        todos.add(new Bombo("pizza_4_quesos", "pizza_italiana", "Cuatro Quesos", "Mozzarella, gorgonzola, parmesano y emmental.", "11.50€"));

        for (Bombo b : todos) {
            if (b.getId().equals(id)) return b;
        }
        return null;
    }

    public void filtrar(String texto) {}
}
