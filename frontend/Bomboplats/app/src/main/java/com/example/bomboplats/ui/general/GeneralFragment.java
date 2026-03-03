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
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Restaurante;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralFragment extends Fragment {

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

        inicializarDiccionario();
        cargarDatosEjemplo();

        adapter = new RestauranteAdapter(new ArrayList<>(listaCompleta));
        recyclerView.setAdapter(adapter);

        return view;
    }
    // Busqueda con palabras clave
    private void inicializarDiccionario() {
        diccionarioEtiquetas = new HashMap<>();
        diccionarioEtiquetas.put("tailandesa", "thai");
        diccionarioEtiquetas.put("tailandia", "thai");
        diccionarioEtiquetas.put("hamburguesa", "burger");
        diccionarioEtiquetas.put("italiana", "pizza");
        diccionarioEtiquetas.put("japonesa", "sushi");
        diccionarioEtiquetas.put("mexicana", "taco");
    }

    private void cargarDatosEjemplo() {
        listaCompleta = new ArrayList<>();
        listaCompleta.add(new Restaurante("thai_food", "Thai Palace", "Comida thai en el centro.", 4.8f, "€€€", Arrays.asList("thai", "asiatica")));
        listaCompleta.add(new Restaurante("burger_place", "The Big Burger", "Mejores hamburguesas.", 4.5f, "€€", Arrays.asList("burger", "fastfood")));
        listaCompleta.add(new Restaurante("pizza_italiana", "Mamma Mia", "Pizzas tradicionales.", 4.7f, "€€", Arrays.asList("pizza", "italiana")));
        listaCompleta.add(new Restaurante("sushi_bar", "Sakura Sushi", "Sushi fresco.", 4.9f, "€€€€", Arrays.asList("sushi", "japonesa")));
        listaCompleta.add(new Restaurante("taco_fiesta", "Taco Fiesta", "Tacos increíbles.", 4.2f, "€", Arrays.asList("taco", "mexicana")));
    }

    // Método público para filtrar desde la Activity
    public void filtrar(String texto) {
        if (listaCompleta == null) return;
        
        List<Restaurante> filtrados = new ArrayList<>();
        String query = texto.toLowerCase().trim();

        if (query.isEmpty()) {
            filtrados.addAll(listaCompleta);
        } else {
            for (Restaurante r : listaCompleta) {
                boolean coincideNombre = r.getNombre().toLowerCase().contains(query);
                
                // 1. Comprobamos etiquetas directas (que empiecen por la búsqueda)
                boolean coincideEtiquetaDirecta = false;
                for (String tag : r.getEtiquetas()) {
                    if (tag.toLowerCase().startsWith(query)) {
                        coincideEtiquetaDirecta = true;
                        break;
                    }
                }

                // 2. Comprobamos palabras clave del diccionario (que empiecen por la búsqueda)
                boolean coincidePalabraClave = false;
                for (Map.Entry<String, String> entry : diccionarioEtiquetas.entrySet()) {
                    // Si la palabra clave (ej: "tailandesa") empieza por lo que el usuario escribe (ej: "tail")
                    if (entry.getKey().startsWith(query)) {
                        String etiquetaAsociada = entry.getValue(); // Obtenemos "thai"
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
