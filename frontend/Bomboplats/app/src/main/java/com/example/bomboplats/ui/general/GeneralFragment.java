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
import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Restaurante;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralFragment extends Fragment implements RestauranteAdapter.OnRestauranteClickListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyError;
    private RestauranteAdapter adapter;
    private List<Restaurante> listaCompleta;
    private FoodRepository foodRepository;
    private Map<String, String> mapaEtiquetas = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        recyclerView = view.findViewById(R.id.rv_restaurantes);
        tvEmptyError = view.findViewById(R.id.tv_empty_error);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        foodRepository = FoodRepository.getInstance(requireContext());
        listaCompleta = foodRepository.getRestaurantes();
        
        cargarMapaEtiquetas();

        if (listaCompleta == null || listaCompleta.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyError.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyError.setVisibility(View.GONE);
            adapter = new RestauranteAdapter(new ArrayList<>(listaCompleta), this);
            recyclerView.setAdapter(adapter);
        }

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

    public void filtrar(String texto) {
        if (listaCompleta == null || adapter == null) return;
        List<Restaurante> filtrados = new ArrayList<>();
        String query = texto.toLowerCase().trim();
        
        if (query.isEmpty()) {
            filtrados.addAll(listaCompleta);
        } else {
            // Buscamos todas las posibles etiquetas finales basadas en lo que el usuario está escribiendo (prefijo)
            List<String> querysExpandidas = new ArrayList<>();
            querysExpandidas.add(query);
            for (Map.Entry<String, String> entry : mapaEtiquetas.entrySet()) {
                if (entry.getKey().startsWith(query)) {
                    querysExpandidas.add(entry.getValue());
                }
            }

            for (Restaurante r : listaCompleta) {
                boolean match = false;
                
                // 1. Comprobar nombre
                if (r.getNombre().toLowerCase().contains(query)) {
                    match = true;
                }
                
                // 2. Comprobar etiquetas del restaurante contra todas las querys expandidas
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

        if (filtrados.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyError.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyError.setVisibility(View.GONE);
            adapter.setFilteredList(filtrados);
        }
    }
}
