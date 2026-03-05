package com.example.bomboplats.ui.general;

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
import com.example.bomboplats.ui.misbombos.FavoritosViewModel;
import java.util.ArrayList;
import java.util.List;

public class BombosFragment extends Fragment implements BomboAdapter.OnBomboClickListener {

    private RecyclerView recyclerViewBombos;
    private RecyclerView recyclerViewFotos;
    private TextView tvEmptyBombos;
    private BomboAdapter adapter;
    private FotoCarruselAdapter fotoAdapter;
    private String restauranteId;
    private List<Bombo> listaBombosRestaurante;
    private CarritoViewModel carritoViewModel;
    private FavoritosViewModel favoritosViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bombos, container, false);

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        favoritosViewModel = new ViewModelProvider(requireActivity()).get(FavoritosViewModel.class);

        // Referencias del encabezado
        TextView tvNombre = view.findViewById(R.id.tv_restaurante_nombre);
        TextView tvUbicacion = view.findViewById(R.id.tv_restaurante_ubicacion);
        TextView tvDescripcion = view.findViewById(R.id.tv_restaurante_descripcion);
        recyclerViewFotos = view.findViewById(R.id.rv_restaurante_fotos);
        
        // Referencias de la lista
        recyclerViewBombos = view.findViewById(R.id.rv_bombos);
        tvEmptyBombos = view.findViewById(R.id.tv_empty_bombos);
        recyclerViewBombos.setLayoutManager(new LinearLayoutManager(getContext()));

        // Recuperar datos
        if (getArguments() != null) {
            restauranteId = getArguments().getString("restauranteId");
            tvNombre.setText(getArguments().getString("nombre"));
            tvUbicacion.setText(getArguments().getString("ubicacion"));
            tvDescripcion.setText(getArguments().getString("descripcion"));
            
            List<String> fotos = getArguments().getStringArrayList("fotos");
            if (fotos != null) {
                fotoAdapter = new FotoCarruselAdapter(fotos);
                recyclerViewFotos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                recyclerViewFotos.setAdapter(fotoAdapter);
            }
        }

        cargarDatosEjemplo();
        updateUI(listaBombosRestaurante);

        return view;
    }

    private void cargarDatosEjemplo() {
        if (listaBombosRestaurante != null && !listaBombosRestaurante.isEmpty()) return;

        List<Bombo> todosLosBombos = new ArrayList<>();
        todosLosBombos.add(new Bombo("pad_thai", "thai_food", "Pad Thai Classic", "Fideos de arroz con gambas y cacahuetes.", "12.50€"));
        todosLosBombos.add(new Bombo("curry_verde", "thai_food", "Green Curry", "Curry verde picante con leche de coco.", "13.90€"));
        todosLosBombos.add(new Bombo("classic_burger", "burger_place", "Clásica con Queso", "Ternera, cheddar, lechuga y tomate.", "10.50€"));
        todosLosBombos.add(new Bombo("bbq_burger", "burger_place", "BBQ Special", "Ternera, bacon y mucha salsa barbacoa.", "11.90€"));
        todosLosBombos.add(new Bombo("pizza_margherita", "pizza_italiana", "Pizza Margherita", "Tomate, mozzarella fresca y albahaca.", "9.50€"));
        todosLosBombos.add(new Bombo("pizza_4_quesos", "pizza_italiana", "Cuatro Quesos", "Mozzarella, gorgonzola, parmesano y emmental.", "11.50€"));

        listaBombosRestaurante = new ArrayList<>();
        for (Bombo b : todosLosBombos) {
            if (b.getRestauranteId() != null && b.getRestauranteId().equals(restauranteId)) {
                listaBombosRestaurante.add(b);
            }
        }
    }

    @Override
    public void onBomboClick(Bombo bombo) {
        DetalleBomboFragment fragment = new DetalleBomboFragment();
        Bundle args = new Bundle();
        args.putString("bomboId", bombo.getId());
        args.putString("nombre", bombo.getNombre());
        args.putString("precio", bombo.getPrecio());
        args.putString("desc", bombo.getDescripcion());
        fragment.setArguments(args);

        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
        }
    }

    @Override
    public void onFavoritoClick(Bombo bombo) {
        favoritosViewModel.toggleFavorito(bombo.getId());
    }

    @Override
    public void onAgregarCarritoClick(Bombo bombo) {
        carritoViewModel.agregarAlCarrito(bombo.getId(), 1);
        Toast.makeText(getContext(), "¡" + bombo.getNombre() + " añadido al carrito!", Toast.LENGTH_SHORT).show();
    }

    public void filtrar(String texto) {
        if (listaBombosRestaurante == null) return;
        List<Bombo> filtrados = new ArrayList<>();
        String query = texto.toLowerCase().trim();
        if (query.isEmpty()) {
            filtrados.addAll(listaBombosRestaurante);
        } else {
            for (Bombo b : listaBombosRestaurante) {
                if (b.getNombre().toLowerCase().contains(query)) {
                    filtrados.add(b);
                }
            }
        }
        updateUI(filtrados);
    }

    private void updateUI(List<Bombo> lista) {
        if (lista == null || lista.isEmpty()) {
            recyclerViewBombos.setVisibility(View.GONE);
            tvEmptyBombos.setVisibility(View.VISIBLE);
        } else {
            recyclerViewBombos.setVisibility(View.VISIBLE);
            tvEmptyBombos.setVisibility(View.GONE);
            
            // Si el adaptador no existe, lo creamos. Si existe, solo actualizamos su lista.
            if (adapter == null) {
                adapter = new BomboAdapter(lista, this, favoritosViewModel);
            } else {
                adapter.setFilteredList(lista);
            }
            // LA CLAVE ESTÁ AQUÍ: Siempre (re)asignamos el adaptador al RecyclerView
            recyclerViewBombos.setAdapter(adapter);
        }
    }
}
