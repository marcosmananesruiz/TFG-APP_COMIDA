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
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.cuenta.UserViewModel;
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
    private UserViewModel userViewModel;
    private FoodRepository foodRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bombos, container, false);

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        foodRepository = FoodRepository.getInstance(requireContext());

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

        // Cargar bombos desde el repositorio
        if (restauranteId != null) {
            listaBombosRestaurante = foodRepository.getBombosPorRestaurante(restauranteId);
        } else {
            listaBombosRestaurante = new ArrayList<>();
        }
        
        updateUI(listaBombosRestaurante);

        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), favs -> {
            if (adapter != null) adapter.notifyDataSetChanged();
        });

        return view;
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
        userViewModel.toggleFavorito(bombo.getId());
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
                boolean matchEtiqueta = false;
                if (b.getEtiquetas() != null) {
                    for (String tag : b.getEtiquetas()) {
                        if (tag.toLowerCase().contains(query)) {
                            matchEtiqueta = true;
                            break;
                        }
                    }
                }
                
                if (b.getNombre().toLowerCase().contains(query) || matchEtiqueta) {
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
            
            if (adapter == null) {
                adapter = new BomboAdapter(lista, this, userViewModel);
            } else {
                adapter.setFilteredList(lista);
            }
            recyclerViewBombos.setAdapter(adapter);
        }
    }
}
