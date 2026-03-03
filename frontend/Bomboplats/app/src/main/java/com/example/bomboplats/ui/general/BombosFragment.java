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
import com.example.bomboplats.data.model.Restaurante;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BombosFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmptyBombos;
    private TextView tvNombre, tvUbicacion, tvDescripcion;
    private RecyclerView rvFotos;
    private BomboAdapter adapter;
    private String restauranteId;
    private List<Bombo> listaBombosRestaurante;
    private Restaurante restauranteActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bombos, container, false);

        // Referencias a la info del restaurante
        tvNombre = view.findViewById(R.id.tv_restaurante_nombre);
        tvUbicacion = view.findViewById(R.id.tv_restaurante_ubicacion);
        tvDescripcion = view.findViewById(R.id.tv_restaurante_descripcion);
        rvFotos = view.findViewById(R.id.rv_restaurante_fotos);

        // RecyclerView de bombos
        recyclerView = view.findViewById(R.id.rv_bombos);
        tvEmptyBombos = view.findViewById(R.id.tv_empty_bombos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            restauranteId = getArguments().getString("restauranteId");
        }

        cargarDatosEjemplo();
        mostrarInfoRestaurante();
        updateUI(listaBombosRestaurante);

        return view;
    }

    private void mostrarInfoRestaurante() {
        if (restauranteActual != null) {
            tvNombre.setText(restauranteActual.getNombre());
            tvUbicacion.setText(restauranteActual.getUbicacion());
            tvDescripcion.setText(restauranteActual.getDescripcion());

            RestauranteFotoAdapter fotoAdapter = new RestauranteFotoAdapter(restauranteActual.getFotos());
            rvFotos.setAdapter(fotoAdapter);
        }
    }

    private void cargarDatosEjemplo() {
        // En una app real esto vendría de una base de datos o API
        List<Restaurante> restaurantes = new ArrayList<>();
        restaurantes.add(new Restaurante("thai_food", "Thai Palace", "Comida tailandesa auténtica en un ambiente acogedor. Disfruta de nuestros currys y fideos.", "Calle Principal 123, Madrid", 4.8f, "€€€", Arrays.asList("thai", "asiatica"), Arrays.asList(android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_camera)));
        restaurantes.add(new Restaurante("burger_place", "The Big Burger", "Las hamburguesas más grandes y sabrosas de la ciudad. Carne de primera calidad.", "Avenida de la Libertad 45, Barcelona", 4.5f, "€€", Arrays.asList("burger", "fastfood"), Arrays.asList(android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_camera)));
        restaurantes.add(new Restaurante("pizza_italiana", "Mamma Mia", "Pizzas artesanas al horno de leña siguiendo las recetas tradicionales italianas.", "Plaza Mayor 10, Sevilla", 4.7f, "€€", Arrays.asList("pizza", "italiana"), Arrays.asList(android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_camera)));

        for (Restaurante r : restaurantes) {
            if (r.getId().equals(restauranteId)) {
                restauranteActual = r;
                break;
            }
        }

        List<Bombo> todosLosBombos = new ArrayList<>();
        // Thai Food
        todosLosBombos.add(new Bombo("pad_thai", "thai_food", "Pad Thai Classic", "Fideos de arroz con gambas.", "12.50€"));
        todosLosBombos.add(new Bombo("curry_verde", "thai_food", "Green Curry", "Curry verde picante.", "13.90€"));
        todosLosBombos.add(new Bombo("som_tam", "thai_food", "Ensalada Som Tam", "Ensalada de papaya verde.", "8.50€"));
        todosLosBombos.add(new Bombo("tom_yum", "thai_food", "Sopa Tom Yum", "Sopa picante de langostinos.", "10.00€"));
        todosLosBombos.add(new Bombo("satay_pollo", "thai_food", "Satay de Pollo", "Brochetas con salsa de cacahuete.", "7.50€"));
        
        // Burger Place
        todosLosBombos.add(new Bombo("classic_burger", "burger_place", "Clásica con Queso", "Ternera, cheddar, lechuga.", "10.50€"));
        todosLosBombos.add(new Bombo("bbq_burger", "burger_place", "BBQ Special", "Ternera, bacon, salsa barbacoa.", "11.90€"));
        todosLosBombos.add(new Bombo("veggie_burger", "burger_place", "Veggie Delight", "Hamburguesa de garbanzos y espinacas.", "9.50€"));
        todosLosBombos.add(new Bombo("chicken_burger", "burger_place", "Crispy Chicken", "Pollo crujiente con mayonesa.", "10.00€"));
        todosLosBombos.add(new Bombo("double_cheese", "burger_place", "Doble Queso", "Doble ración de carne y queso.", "13.50€"));

        // Pizza Italiana
        todosLosBombos.add(new Bombo("pizza_margherita", "pizza_italiana", "Pizza Margherita", "Tomate, mozzarella fresca.", "9.50€"));
        todosLosBombos.add(new Bombo("pizza_4_quesos", "pizza_italiana", "Cuatro Quesos", "Varios tipos de queso.", "11.50€"));
        todosLosBombos.add(new Bombo("pizza_carbonara", "pizza_italiana", "Carbonara", "Nata, bacon y cebolla.", "10.50€"));
        todosLosBombos.add(new Bombo("pizza_prosciutto", "pizza_italiana", "Prosciutto", "Jamón cocido y champiñones.", "10.00€"));
        todosLosBombos.add(new Bombo("calzone", "pizza_italiana", "Calzone Tradicional", "Pizza rellena clásica.", "12.00€"));
        todosLosBombos.add(new Bombo("lasagna", "pizza_italiana", "Lasagna Bolognese", "Pasta fresca con carne.", "9.00€"));

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
