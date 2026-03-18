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
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.BomboConCantidad;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import com.example.bomboplats.ui.general.DetalleBomboFragment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CarritoFragment extends Fragment implements CarritoAdapter.OnCarritoActionListener {

    private CarritoViewModel carritoViewModel;
    private UserViewModel userViewModel;
    private FoodRepository foodRepository;
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
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        foodRepository = FoodRepository.getInstance(requireContext());

        adapter = new CarritoAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Observar cambios en el carrito
        carritoViewModel.getItemsCarrito().observe(getViewLifecycleOwner(), items -> {
            userViewModel.setCarritoUI(items);
            actualizarUI();
        });

        // Observar cambios en favoritos
        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), favoritos -> {
            actualizarUI();
        });

        btnPagar.setOnClickListener(v -> {
            RealizarEnvioFragment fragment = new RealizarEnvioFragment();
            if (getActivity() instanceof GeneralActivity) {
                ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
            }
        });

        return view;
    }

    private void actualizarUI() {
        Map<String, Integer> items = carritoViewModel.getItemsCarrito().getValue();
        List<String> favoritosList = userViewModel.getFavoritos().getValue();
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
            String key = entry.getKey(); // Formato "restauranteId:bomboId"
            int cantidad = entry.getValue();
            
            String[] parts = key.split(":");
            String bomboId = parts.length == 2 ? parts[1] : key;
            
            Bombo b = buscarBomboPorId(bomboId);
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
    public void onRestarClick(String itemKey) {
        carritoViewModel.removerDelCarrito(itemKey);
    }

    @Override
    public void onFavoritoClick(String itemKey) {
        String[] parts = itemKey.split(":");
        if (parts.length == 2) {
            userViewModel.toggleFavorito(parts[0], parts[1]);
        }
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
        return foodRepository.getBomboPorId(id);
    }

    public void filtrar(String texto) {}
}
