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
    private List<Bombo> listaBombosRestaurante = new ArrayList<>();
    private CarritoViewModel carritoViewModel;
    private UserViewModel userViewModel;
    private FoodRepository foodRepository;

    private static final String DEFAULT_RESTAURANTE_IMAGE = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/restaurantes/default_0.jpg";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bombos, container, false);

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        foodRepository = FoodRepository.getInstance(requireContext());

        TextView tvNombre = view.findViewById(R.id.tv_restaurante_nombre);
        TextView tvUbicacion = view.findViewById(R.id.tv_restaurante_ubicacion);
        TextView tvDescripcion = view.findViewById(R.id.tv_restaurante_descripcion);
        recyclerViewFotos = view.findViewById(R.id.rv_restaurante_fotos);
        
        recyclerViewBombos = view.findViewById(R.id.rv_bombos);
        tvEmptyBombos = view.findViewById(R.id.tv_empty_bombos);
        recyclerViewBombos.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            restauranteId = getArguments().getString("restauranteId");
            tvNombre.setText(getArguments().getString("nombre"));
            tvUbicacion.setText(getArguments().getString("ubicacion"));
            tvDescripcion.setText(getArguments().getString("descripcion"));
            
            List<String> fotos = getArguments().getStringArrayList("fotos");
            fotoAdapter = new FotoCarruselAdapter(fotos, DEFAULT_RESTAURANTE_IMAGE);
            recyclerViewFotos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewFotos.setAdapter(fotoAdapter);
        }

        // Si ya tenemos el ID, intentamos cargar los platos que haya en caché inmediatamente
        if (restauranteId != null) {
            listaBombosRestaurante = foodRepository.getBombosPorRestaurante(restauranteId);
            if (listaBombosRestaurante != null && !listaBombosRestaurante.isEmpty()) {
                updateUI(listaBombosRestaurante);
            }
        }

        // OBSERVADOR CLAVE: Escuchamos cambios en el repositorio para actualizar los platos en tiempo real
        foodRepository.getRestaurantesLiveData().observe(getViewLifecycleOwner(), restaurantes -> {
            if (restauranteId != null) {
                listaBombosRestaurante = foodRepository.getBombosPorRestaurante(restauranteId);
                updateUI(listaBombosRestaurante);
            }
        });

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
        userViewModel.toggleFavorito(bombo);
    }

    @Override
    public void onAgregarCarritoClick(Bombo bombo) {
        carritoViewModel.agregarAlCarrito(bombo, 1, new ArrayList<>());
        String mensaje = getString(R.string.carrito_item_added, 1, bombo.getNombre());
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    public void filtrar(String texto) {
        if (listaBombosRestaurante == null) return;
        List<Bombo> filtrados = new ArrayList<>();
        String query = texto.toLowerCase().trim();
        
        if (query.isEmpty()) {
            filtrados.addAll(listaBombosRestaurante);
        } else {
            for (Bombo b : listaBombosRestaurante) {
                boolean match = b.getNombre().toLowerCase().contains(query);
                
                if (!match && b.getEtiquetas() != null) {
                    for (String tag : b.getEtiquetas()) {
                        if (tag.toLowerCase().contains(query)) {
                            match = true;
                            break;
                        }
                    }
                }
                
                if (match) {
                    filtrados.add(match ? b : null); // Evitar duplicados si ya match es true
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
            // Importante: volver a asignar el adaptador al RecyclerView porque la vista se recrea al volver atrás
            recyclerViewBombos.setAdapter(adapter);
        }
    }
}
