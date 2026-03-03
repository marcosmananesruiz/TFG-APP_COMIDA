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
import com.example.bomboplats.data.model.Bombo;
import java.util.ArrayList;
import java.util.List;

public class BombosFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmptyBombos;
    private BomboAdapter adapter;
    private String restauranteId;
    private List<Bombo> listaBombosRestaurante;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bombos, container, false);

        recyclerView = view.findViewById(R.id.rv_bombos);
        tvEmptyBombos = view.findViewById(R.id.tv_empty_bombos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            restauranteId = getArguments().getString("restauranteId");
        }

        cargarDatosEjemplo();
        updateUI(listaBombosRestaurante);

        return view;
    }

    private void cargarDatosEjemplo() {
        List<Bombo> todosLosBombos = new ArrayList<>();
        todosLosBombos.add(new Bombo("pad_thai", "thai_food", "Pad Thai Classic", "Fideos de arroz con gambas.", "12.50€"));
        todosLosBombos.add(new Bombo("curry_verde", "thai_food", "Green Curry", "Curry verde picante.", "13.90€"));
        todosLosBombos.add(new Bombo("classic_burger", "burger_place", "Clásica con Queso", "Ternera, cheddar, lechuga.", "10.50€"));
        todosLosBombos.add(new Bombo("bbq_burger", "burger_place", "BBQ Special", "Ternera, bacon, salsa barbacoa.", "11.90€"));
        todosLosBombos.add(new Bombo("pizza_margherita", "pizza_italiana", "Pizza Margherita", "Tomate, mozzarella fresca.", "9.50€"));
        todosLosBombos.add(new Bombo("pizza_4_quesos", "pizza_italiana", "Cuatro Quesos", "Varios tipos de queso.", "11.50€"));

        listaBombosRestaurante = new ArrayList<>();
        for (Bombo b : todosLosBombos) {
            if (b.getRestauranteId().equals(restauranteId)) {
                listaBombosRestaurante.add(b);
            }
        }
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
        if (lista.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyBombos.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyBombos.setVisibility(View.GONE);
            if (adapter == null) {
                adapter = new BomboAdapter(lista);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.setFilteredList(lista);
            }
        }
    }
}
