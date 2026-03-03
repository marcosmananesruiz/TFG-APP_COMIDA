package com.example.bomboplats.ui.general;

import android.content.res.AssetManager;
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
import com.example.bomboplats.data.model.Restaurante;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralFragment extends Fragment implements RestauranteAdapter.OnRestauranteClickListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyError;
    private RestauranteAdapter adapter;
    private List<Restaurante> listaCompleta;
    private Map<String, String> diccionarioEtiquetas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        recyclerView = view.findViewById(R.id.rv_restaurantes);
        tvEmptyError = view.findViewById(R.id.tv_empty_error);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        inicializarDiccionarioDesdeAssets();
        cargarDatosEjemplo();

        adapter = new RestauranteAdapter(new ArrayList<>(listaCompleta), this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void inicializarDiccionarioDesdeAssets() {
        diccionarioEtiquetas = new HashMap<>();
        AssetManager am = getContext().getAssets();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(am.open("etiquetas.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    diccionarioEtiquetas.put(parts[0].trim().toLowerCase(), parts[1].trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDatosEjemplo() {
        listaCompleta = new ArrayList<>();
        listaCompleta.add(new Restaurante("thai_food", "Thai Palace", "Comida thai en el centro.", 4.8f, "€€€", Arrays.asList("thai", "asiatica")));
        listaCompleta.add(new Restaurante("burger_place", "The Big Burger", "Mejores hamburguesas.", 4.5f, "€€", Arrays.asList("burger", "fastfood")));
        listaCompleta.add(new Restaurante("pizza_italiana", "Mamma Mia", "Pizzas tradicionales.", 4.7f, "€€", Arrays.asList("pizza", "italiana")));
        listaCompleta.add(new Restaurante("sushi_bar", "Sakura Sushi", "Sushi fresco.", 4.9f, "€€€€", Arrays.asList("sushi", "japonesa")));
        listaCompleta.add(new Restaurante("taco_fiesta", "Taco Fiesta", "Tacos increíbles.", 4.2f, "€", Arrays.asList("taco", "mexicana")));
    }

    @Override
    public void onRestauranteClick(Restaurante restaurante) {
        BombosFragment fragment = new BombosFragment();
        Bundle args = new Bundle();
        args.putString("restauranteId", restaurante.getId());
        fragment.setArguments(args);

        // Usamos el nuevo método de la Activity que ya tiene las animaciones configuradas
        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
        }
    }

    public void filtrar(String texto) {
        if (listaCompleta == null) return;
        
        List<Restaurante> filtrados = new ArrayList<>();
        String query = texto.toLowerCase().trim();

        if (query.isEmpty()) {
            filtrados.addAll(listaCompleta);
        } else {
            for (Restaurante r : listaCompleta) {
                boolean coincideNombre = r.getNombre().toLowerCase().contains(query);
                
                boolean coincideEtiquetaDirecta = false;
                for (String tag : r.getEtiquetas()) {
                    if (tag.toLowerCase().startsWith(query)) {
                        coincideEtiquetaDirecta = true;
                        break;
                    }
                }

                boolean coincidePalabraClave = false;
                for (Map.Entry<String, String> entry : diccionarioEtiquetas.entrySet()) {
                    if (entry.getKey().startsWith(query)) {
                        String etiquetaAsociada = entry.getValue();
                        if (r.getEtiquetas().contains(etiquetaAsociada)) {
                            coincidePalabraClave = true;
                            break;
                        }
                    }
                }

                if (coincideNombre || coincideEtiquetaDirecta || coincidePalabraClave) {
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
            if (adapter != null) {
                adapter.setFilteredList(filtrados);
            }
        }
    }
}
