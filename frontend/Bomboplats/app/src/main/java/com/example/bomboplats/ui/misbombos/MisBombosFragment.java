package com.example.bomboplats.ui.misbombos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.general.BomboAdapter;
import com.example.bomboplats.ui.general.DetalleBomboFragment;
import java.util.ArrayList;
import java.util.List;

public class MisBombosFragment extends Fragment implements BomboAdapter.OnBomboClickListener {

    private RecyclerView recyclerView;
    private BomboAdapter adapter;
    private FavoritosViewModel favoritosViewModel;
    private CarritoViewModel carritoViewModel;
    private TextView tvEmptyFavoritos;
    private List<Bombo> listaFavoritosCompleta = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_misbombosfavoritos, container, false);

        recyclerView = view.findViewById(R.id.rv_mis_bombos);
        tvEmptyFavoritos = view.findViewById(R.id.tv_empty_favoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favoritosViewModel = new ViewModelProvider(requireActivity()).get(FavoritosViewModel.class);
        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);

        // Observar los cambios en la lista de favoritos
        favoritosViewModel.getIdsFavoritos().observe(getViewLifecycleOwner(), idsFavoritos -> {
            actualizarListaFavoritos(idsFavoritos);
        });

        return view;
    }

    private void actualizarListaFavoritos(List<String> idsFavoritos) {
        List<Bombo> todosLosBombos = obtenerTodosLosBombos();
        listaFavoritosCompleta.clear();
        for (Bombo b : todosLosBombos) {
            if (idsFavoritos.contains(b.getId())) {
                listaFavoritosCompleta.add(b);
            }
        }

        mostrarResultados(listaFavoritosCompleta);
    }

    private void mostrarResultados(List<Bombo> lista) {
        if (lista.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyFavoritos.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyFavoritos.setVisibility(View.GONE);
            
            if (adapter == null) {
                adapter = new BomboAdapter(new ArrayList<>(lista), this, favoritosViewModel);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.setFilteredList(new ArrayList<>(lista));
            }
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

    @Override
    public void onFavoritoClick(Bombo b) {
        favoritosViewModel.toggleFavorito(b.getId());
    }

    @Override
    public void onAgregarCarritoClick(Bombo b) {
        carritoViewModel.agregarAlCarrito(b.getId(), 1);
        Toast.makeText(getContext(), "¡" + b.getNombre() + " añadido al carrito!", Toast.LENGTH_SHORT).show();
    }

    public void filtrar(String texto) {
        if (listaFavoritosCompleta == null) return;
        
        List<Bombo> filtrados = new ArrayList<>();
        String query = texto.toLowerCase().trim();

        if (query.isEmpty()) {
            filtrados.addAll(listaFavoritosCompleta);
        } else {
            for (Bombo b : listaFavoritosCompleta) {
                if (b.getNombre().toLowerCase().contains(query)) {
                    filtrados.add(b);
                }
            }
        }
        
        mostrarResultados(filtrados);
    }

    private List<Bombo> obtenerTodosLosBombos() {
        List<Bombo> todos = new ArrayList<>();
        todos.add(new Bombo("pad_thai", "thai_food", "Pad Thai Classic", "Fideos de arroz con gambas y cacahuetes.", "12.50€"));
        todos.add(new Bombo("curry_verde", "thai_food", "Green Curry", "Curry verde picante con leche de coco.", "13.90€"));
        todos.add(new Bombo("classic_burger", "burger_place", "Clásica con Queso", "Ternera, cheddar, lechuga y tomate.", "10.50€"));
        todos.add(new Bombo("bbq_burger", "burger_place", "BBQ Special", "Ternera, bacon y mucha salsa barbacoa.", "11.90€"));
        todos.add(new Bombo("pizza_margherita", "pizza_italiana", "Pizza Margherita", "Tomate, mozzarella fresca y albahaca.", "9.50€"));
        todos.add(new Bombo("pizza_4_quesos", "pizza_italiana", "Cuatro Quesos", "Mozzarella, gorgonzola, parmesano y emmental.", "11.50€"));
        return todos;
    }
}
