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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, String> mapaEtiquetas = new HashMap<>();

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
            if (fotos != null) {
                fotoAdapter = new FotoCarruselAdapter(fotos);
                recyclerViewFotos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                recyclerViewFotos.setAdapter(fotoAdapter);
            }
        }

        cargarMapaEtiquetas();

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

    private void cargarMapaEtiquetas() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requireContext().getAssets().open("etiquetas.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    mapaEtiquetas.put(parts[0].trim().toLowerCase(), parts[1].trim().toLowerCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        userViewModel.toggleFavorito(bombo.getRestauranteId(), bombo.getId());
    }

    @Override
    public void onAgregarCarritoClick(Bombo bombo) {
        String itemKey = bombo.getRestauranteId() + ":" + bombo.getId();
        carritoViewModel.agregarAlCarrito(itemKey, 1);
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
            // Buscamos todas las posibles etiquetas finales basadas en prefijos del mapa
            List<String> querysExpandidas = new ArrayList<>();
            querysExpandidas.add(query);
            for (Map.Entry<String, String> entry : mapaEtiquetas.entrySet()) {
                if (entry.getKey().startsWith(query)) {
                    querysExpandidas.add(entry.getValue());
                }
            }

            for (Bombo b : listaBombosRestaurante) {
                boolean match = false;
                
                // 1. Comprobar nombre
                if (b.getNombre().toLowerCase().contains(query)) {
                    match = true;
                }
                
                // 2. Comprobar etiquetas expandidas
                if (!match && b.getEtiquetas() != null) {
                    for (String tag : b.getEtiquetas()) {
                        String tagLower = tag.toLowerCase();
                        for (String qExp : querysExpandidas) {
                            if (tagLower.contains(qExp)) {
                                match = true;
                                break;
                            }
                        }
                        if (match) break;
                    }
                }
                
                if (match) {
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
