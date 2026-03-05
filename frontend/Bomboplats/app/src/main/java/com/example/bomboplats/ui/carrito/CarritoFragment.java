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
import com.example.bomboplats.ui.general.DetalleBomboFragment;
import com.example.bomboplats.ui.misbombos.FavoritosViewModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CarritoFragment extends Fragment implements CarritoAdapter.OnCarritoActionListener {

    private CarritoViewModel carritoViewModel;
    private FavoritosViewModel favoritosViewModel;
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
        favoritosViewModel = new ViewModelProvider(requireActivity()).get(FavoritosViewModel.class);

        adapter = new CarritoAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Observar cambios en el carrito
        carritoViewModel.getItemsCarrito().observe(getViewLifecycleOwner(), items -> {
            actualizarUI();
        });

        // Observar cambios en favoritos
        favoritosViewModel.getIdsFavoritos().observe(getViewLifecycleOwner(), favoritos -> {
            actualizarUI();
        });

        return view;
    }

    private void actualizarUI() {
        Map<String, Integer> items = carritoViewModel.getItemsCarrito().getValue();
        List<String> favoritosList = favoritosViewModel.getIdsFavoritos().getValue();
        Set<String> favoritosSet = favoritosList != null ? new HashSet<>(favoritosList) : new HashSet<>();

        if (items == null || items.isEmpty()) {
            mostrarCarritoVacio(true);
        } else {
            mostrarCarritoVacio(false);
            actualizarListaVisual(items, favoritosSet);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void actualizarListaVisual(Map<String, Integer> items, Set<String> favoritos) {
        List<BomboConCantidad> listaFinal = new ArrayList<>();
        double totalCompra = 0.0;

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String id = entry.getKey();
            int cantidad = entry.getValue();
            
            Bombo b = buscarBomboPorId(id);
            if (b != null) {
                listaFinal.add(new BomboConCantidad(b, cantidad));
                try {
                    String precioLimpio = b.getPrecio().replace("€", "").replace(",", ".").trim();
                    totalCompra += Double.parseDouble(precioLimpio) * cantidad;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        adapter.actualizarLista(listaFinal, favoritos);
        tvTotalPrecio.setText(String.format(Locale.getDefault(), "%.2f€", totalCompra));
    }

    @Override
    public void onRestarClick(String bomboId) {
        carritoViewModel.removerDelCarrito(bomboId);
    }

    @Override
    public void onFavoritoClick(String bomboId) {
        favoritosViewModel.toggleFavorito(bomboId);
    }

    @Override
    public void onBomboClick(Bombo b) {
        DetalleBomboFragment fragment = new DetalleBomboFragment();
        Bundle args = new Bundle();
        args.putString("bomboId", b.getId());
        args.putString("nombre", b.getNombre());
        args.putString("precio", b.getPrecio());
        args.putString("desc", b.getDescripcion());
        fragment.setArguments(args);

        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
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
        
        todos.add(new Bombo("som_tam", "thai_food", "Ensalada Som Tam", "Ensalada de papaya verde.", "8.50€"));
        todos.add(new Bombo("tom_yum", "thai_food", "Sopa Tom Yum", "Sopa picante de langostinos.", "10.00€"));
        todos.add(new Bombo("satay_pollo", "thai_food", "Satay de Pollo", "Brochetas con salsa de cacahuete.", "7.50€"));

        for (Bombo b : todos) {
            if (b.getId().equals(id)) return b;
        }
        return null;
    }

    public void filtrar(String texto) {}
}
