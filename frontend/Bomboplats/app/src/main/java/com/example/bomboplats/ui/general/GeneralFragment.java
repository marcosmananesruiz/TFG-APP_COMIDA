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

    private void inicializarDiccionarioDesdeAssets() {
        diccionarioEtiquetas = new HashMap<>();
        if (getContext() == null) return;
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
        // Añadimos ubicación y una lista de fotos para el carrusel a cada restaurante
        listaCompleta.add(new Restaurante("thai_food", "Thai Palace", "Auténtica comida tailandesa en el centro.", 4.8f, "€€€", Arrays.asList("thai", "asiatica"), "Calle Real 123, Madrid", Arrays.asList("thai_food", "thai_food_2", "thai_food_3")));
        listaCompleta.add(new Restaurante("burger_place", "The Big Burger", "Las mejores hamburguesas de la ciudad.", 4.5f, "€€", Arrays.asList("burger", "fastfood"), "Av. Libertad 45, Madrid", Arrays.asList("burger_place", "burger_place_2")));
        listaCompleta.add(new Restaurante("pizza_italiana", "Mamma Mia", "Pizzas al horno de leña tradicionales.", 4.7f, "€€", Arrays.asList("pizza", "italiana"), "Plaza Mayor 5, Madrid", Arrays.asList("pizza_italiana", "pizza_italiana_2")));
        listaCompleta.add(new Restaurante("sushi_bar", "Sakura Sushi", "Sushi fresco y variado todos los días.", 4.9f, "€€€€", Arrays.asList("sushi", "japonesa"), "Calle Pez 10, Madrid", Arrays.asList("sushi_bar", "sushi_bar_2")));
        listaCompleta.add(new Restaurante("taco_fiesta", "Taco Fiesta", "Tacos, burritos y margaritas increíbles.", 4.2f, "€", Arrays.asList("taco", "mexicana"), "Calle Luna 22, Madrid", Arrays.asList("taco_fiesta", "taco_fiesta_2")));
    }

    @Override
    public void onRestauranteClick(Restaurante restaurante) {
        BombosFragment fragment = new BombosFragment();
        Bundle args = new Bundle();
        args.putString("restauranteId", restaurante.getId());
        args.putString("nombre", restaurante.getNombre());
        args.putString("ubicacion", restaurante.getUbicacion());
        args.putString("descripcion", restaurante.getDescripcion());
        // Pasamos las fotos como un ArrayList de Strings
        args.putStringArrayList("fotos", new ArrayList<>(restaurante.getFotosCarrusel()));
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
            for (Restaurante r : listaCompleta) {
                if (r.getNombre().toLowerCase().contains(query) || r.getEtiquetas().contains(query)) {
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
