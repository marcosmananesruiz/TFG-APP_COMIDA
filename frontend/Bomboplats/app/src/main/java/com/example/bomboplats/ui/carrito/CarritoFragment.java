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
import com.example.bomboplats.data.model.StagedBombo;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import com.example.bomboplats.ui.general.DetalleBomboFragment;
import com.example.bomboplats.ui.general.StagedBomboFragment;

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

        adapter = new CarritoAdapter(new ArrayList<>(), new HashSet<>(), this, this.userViewModel);
        recyclerView.setAdapter(adapter);

        // Observar cambios en el carrito
        carritoViewModel.getItemsCarrito().observe(getViewLifecycleOwner(), items -> {
            userViewModel.setCarritoUI(items);
            actualizarUI(items);
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

    private void actualizarUI(List<StagedBombo> items) {
        List<Bombo> favoritosList = userViewModel.getFavoritos().getValue();
        Set<Bombo> favoritosSet = new HashSet<>();
        if (favoritosList != null) {
            favoritosSet.addAll(favoritosList);
        }

        if (items == null || items.isEmpty()) {
            mostrarCarritoVacio(true);
        } else {
            mostrarCarritoVacio(false);
            actualizarListaVisual(items, favoritosSet);
        }
    }

    private void actualizarUI() {
        this.userViewModel.getCarrito().observe(getViewLifecycleOwner(), this::actualizarUI);
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

    private void actualizarListaVisual(List<StagedBombo> carrito, Set<Bombo> favoritos) {

        float totalCompra = (float) carrito.stream()
                .mapToDouble(stagedBombo -> stagedBombo.getCantidad() * Double.parseDouble(stagedBombo.getBombo().getPrecio()))
                .sum();


        adapter.actualizarLista(carrito, favoritos);
        tvTotalPrecio.setText(String.format(Locale.getDefault(), "%.2f€", totalCompra));
    }

    @Override
    public void onRestarClick(StagedBombo bombo) {
        carritoViewModel.removerDelCarrito(bombo);
        this.actualizarUI();
    }

    @Override
    public void onFavoritoClick(Bombo bombo) {
        this.userViewModel.toggleFavorito(bombo);
    }

    @Override
    public void onBomboClick(StagedBombo b) {
        StagedBomboFragment fragment = new StagedBomboFragment();
        Bundle args = new Bundle();
        args.putInt("staged_bombo", b.getId());
        fragment.setArguments(args);

        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
        }
    }

    private StagedBombo obtenerStagedBombo(Bombo bombo) {
        List<StagedBombo> stagedBombos = this.carritoViewModel.getItemsCarrito().getValue();
        if (stagedBombos == null) return null;
        return stagedBombos.stream().filter(stagedBombo -> stagedBombo.getBombo().equals(bombo)).findFirst().orElse(null);
    }

    public void filtrar(String texto) {
        List<StagedBombo> stagedBombos = this.carritoViewModel.getItemsCarrito().getValue();
        if (stagedBombos == null || this.adapter == null) return;

        List<StagedBombo> filteredBombos = stagedBombos.stream()
                .filter(stagedBombo -> stagedBombo.getBombo().getNombre().toLowerCase().contains(texto.toLowerCase())
                                                || stagedBombo.getBombo().getEtiquetas().contains(texto.toLowerCase()))
                .toList();

        this.adapter.actualizarLista(filteredBombos);
    }

    public List<StagedBombo> getCarrito() {
        return this.carritoViewModel.getItemsCarrito().getValue();
    }
}
