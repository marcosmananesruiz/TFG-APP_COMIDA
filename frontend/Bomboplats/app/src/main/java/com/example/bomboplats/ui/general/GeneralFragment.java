package com.example.bomboplats.ui.general;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Restaurante;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragmento para mostrar los restaurantes.
 */
public class GeneralFragment extends Fragment implements RestauranteAdapter.OnRestauranteClickListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyError;
    private RestauranteAdapter adapter;
    private List<Restaurante> listaCompleta = new ArrayList<>();
    private FoodRepository foodRepository;
    private Map<String, String> mapaEtiquetas = new HashMap<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        recyclerView = view.findViewById(R.id.rv_restaurantes);
        tvEmptyError = view.findViewById(R.id.tv_empty_error);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_inicio);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RestauranteAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        foodRepository = FoodRepository.getInstance(requireContext());
        
        // Configurar el refresh manual
        swipeRefreshLayout.setOnRefreshListener(() -> {
            foodRepository.refreshData();
        });
        
        // Observar los cambios en los restaurantes para actualizar la UI automáticamente
        foodRepository.getRestaurantesLiveData().observe(getViewLifecycleOwner(), restaurantes -> {
            // Detener la animación de carga
            swipeRefreshLayout.setRefreshing(false);

            // Actualizar la lista de restaurantes
            if (restaurantes != null && !restaurantes.isEmpty()) {
                listaCompleta = restaurantes;
                listaCompleta.sort(Comparator.comparing(Restaurante::getNombre));
                adapter.setFilteredList(new ArrayList<>(listaCompleta));
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyError.setVisibility(View.GONE);
            // Mostrar mensaje de error si no hay restaurantes
            } else {
                recyclerView.setVisibility(View.GONE);
                tvEmptyError.setVisibility(View.VISIBLE);
            }
        });
        
        // Forzar un refresh al entrar por si acaso
        foodRepository.refreshData();
        cargarMapaEtiquetas();
        return view;
    }

    // Cargar el mapa de etiquetas/tags
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
    public void onRestauranteClick(Restaurante restaurante) {
        BombosFragment fragment = new BombosFragment();
        Bundle args = new Bundle();
        args.putString("restauranteId", restaurante.getId());
        args.putString("nombre", restaurante.getNombre());
        args.putString("ubicacion", restaurante.getUbicacion());
        args.putString("descripcion", restaurante.getDescripcion());
        args.putStringArrayList("fotos", new ArrayList<>(restaurante.getFotos()));
        fragment.setArguments(args);

        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
        }
    }

    // Implementación de filtrar por busqueda
    public void filtrar(String texto) {
        if (listaCompleta == null || adapter == null) return;
        List<Restaurante> filtrados = new ArrayList<>();
        String query = texto.toLowerCase().trim();
        
        if (query.isEmpty()) {
            filtrados.addAll(listaCompleta);
        } else {
            List<String> querysExpandidas = new ArrayList<>();
            querysExpandidas.add(query);
            for (Map.Entry<String, String> entry : mapaEtiquetas.entrySet()) {
                if (entry.getKey().startsWith(query)) {
                    querysExpandidas.add(entry.getValue());
                }
            }

            for (Restaurante r : listaCompleta) {
                boolean match = false;
                if (r.getNombre().toLowerCase().contains(query)) {
                    match = true;
                }
                
                if (!match && r.getEtiquetas() != null) {
                    for (String tag : r.getEtiquetas()) {
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
                    filtrados.add(r);
                }
            }
        }

        // Ordenar por nombre
        if (filtrados.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyError.setVisibility(View.VISIBLE);
        // Mostrar mensaje de error si no hay restaurantes filtrados
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyError.setVisibility(View.GONE);
            adapter.setFilteredList(filtrados);
        }
    }
}
