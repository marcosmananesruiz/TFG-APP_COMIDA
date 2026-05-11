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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

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
    
    private ChipGroup cgCategories;
    private String categoriaSeleccionada = "ENTRANTES";
    private String queryActual = "";

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
        cgCategories = view.findViewById(R.id.cg_categories);
        
        recyclerViewBombos.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configuración de categorías
        setupCategories();

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

        if (restauranteId != null) {
            listaBombosRestaurante = foodRepository.getBombosPorRestaurante(restauranteId);
            aplicarFiltros();
        }

        foodRepository.getRestaurantesLiveData().observe(getViewLifecycleOwner(), restaurantes -> {
            if (restauranteId != null) {
                listaBombosRestaurante = foodRepository.getBombosPorRestaurante(restauranteId);
                aplicarFiltros();
            }
        });

        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), favs -> {
            if (adapter != null) adapter.notifyDataSetChanged();
        });

        return view;
    }

    private void setupCategories() {
        // Seleccionamos "ENTRANTES" por defecto
        cgCategories.check(R.id.chip_entrantes);
        
        cgCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                // Si el usuario intenta desmarcar, volvemos a marcar el anterior o el primero
                group.check(R.id.chip_entrantes);
                return;
            }
            
            int checkedId = checkedIds.get(0);
            Chip chip = group.findViewById(checkedId);
            if (chip != null) {
                categoriaSeleccionada = chip.getText().toString().toUpperCase();
                aplicarFiltros();
            }
        });
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
        this.queryActual = texto.toLowerCase().trim();
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (listaBombosRestaurante == null) return;
        
        List<Bombo> filtrados = new ArrayList<>();
        
        for (Bombo b : listaBombosRestaurante) {
            // Filtro 1: Categoría (basado en el primer tag)
            boolean coincideCategoria = false;
            if (b.getEtiquetas() != null && !b.getEtiquetas().isEmpty()) {
                String primerTag = b.getEtiquetas().get(0).toUpperCase();
                if (primerTag.equals(categoriaSeleccionada)) {
                    coincideCategoria = true;
                }
            }
            
            if (!coincideCategoria) continue;

            // Filtro 2: Texto de búsqueda (si hay alguno)
            if (queryActual.isEmpty()) {
                filtrados.add(b);
            } else {
                boolean matchTexto = b.getNombre().toLowerCase().contains(queryActual);
                if (!matchTexto && b.getEtiquetas() != null) {
                    for (String tag : b.getEtiquetas()) {
                        if (tag.toLowerCase().contains(queryActual)) {
                            matchTexto = true;
                            break;
                        }
                    }
                }
                if (matchTexto) {
                    filtrados.add(b);
                }
            }
        }
        
        updateUI(filtrados);
    }

    private void updateUI(List<Bombo> lista) {
        if (lista == null || lista.isEmpty()) {
            recyclerViewBombos.setVisibility(View.GONE);
            tvEmptyBombos.setText("No hay ningún plato en esta categoría");
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
