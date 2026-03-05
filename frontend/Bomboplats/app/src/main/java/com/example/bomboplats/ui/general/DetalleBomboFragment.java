package com.example.bomboplats.ui.general;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetalleBomboFragment extends Fragment {

    private TextView tvNombre, tvPrecio, tvDescripcion, tvIngredientes, tvAlergenos;
    private RecyclerView rvFotos;
    private String bomboId;
    private Bombo bomboActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_bombo, container, false);

        tvNombre = view.findViewById(R.id.tv_bombo_nombre);
        tvPrecio = view.findViewById(R.id.tv_bombo_precio);
        tvDescripcion = view.findViewById(R.id.tv_bombo_descripcion);
        tvIngredientes = view.findViewById(R.id.tv_bombo_ingredientes);
        tvAlergenos = view.findViewById(R.id.tv_bombo_alergenos);
        rvFotos = view.findViewById(R.id.rv_bombo_fotos);

        if (getArguments() != null) {
            bomboId = getArguments().getString("bomboId");
        }

        cargarDatosEjemplo();
        mostrarInfoBombo();

        return view;
    }

    private void mostrarInfoBombo() {
        if (bomboActual != null) {
            tvNombre.setText(bomboActual.getNombre());
            tvPrecio.setText(bomboActual.getPrecio());
            tvDescripcion.setText(bomboActual.getDescripcion());
            
            if (bomboActual.getIngredientes() != null && !bomboActual.getIngredientes().isEmpty()) {
                tvIngredientes.setText(String.join(", ", bomboActual.getIngredientes()));
            } else {
                tvIngredientes.setText("No especificados");
            }

            if (bomboActual.getAlergenos() != null && !bomboActual.getAlergenos().isEmpty()) {
                tvAlergenos.setText(String.join(", ", bomboActual.getAlergenos()));
            } else {
                tvAlergenos.setText("Sin alérgenos conocidos");
            }

            RestauranteFotoAdapter fotoAdapter = new RestauranteFotoAdapter(bomboActual.getFotos());
            rvFotos.setAdapter(fotoAdapter);
        }
    }

    private void cargarDatosEjemplo() {
        List<Bombo> todosLosBombos = new ArrayList<>();
        
        // Thai Food
        todosLosBombos.add(new Bombo("pad_thai", "thai_food", "Pad Thai Classic", 
            "Fideos de arroz con gambas, tofu, huevo y brotes de soja.", "12.50€",
            Arrays.asList("Fideos de arroz", "Gambas", "Tofu", "Huevo", "Cacahuetes", "Brotes de soja", "Salsa de pescado"),
            Arrays.asList("Crustáceos", "Huevo", "Cacahuetes", "Pescado", "Soja"),
            Arrays.asList(android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_camera)));
        
        todosLosBombos.add(new Bombo("curry_verde", "thai_food", "Green Curry", 
            "Curry verde picante con leche de coco y verduras.", "13.90€",
            Arrays.asList("Leche de coco", "Pasta de curry verde", "Pollo", "Berenjena tailandesa", "Albahaca"),
            Arrays.asList("Pescado", "Lácteos"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("som_tam", "thai_food", "Ensalada Som Tam", 
            "Ensalada de papaya verde con un toque picante.", "8.50€",
            Arrays.asList("Papaya verde", "Tomate", "Judías verdes", "Cacahuetes", "Chile", "Lima"),
            Arrays.asList("Cacahuetes", "Pescado"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("tom_yum", "thai_food", "Sopa Tom Yum", 
            "Sopa picante y agria de langostinos con hierba limón.", "10.00€",
            Arrays.asList("Langostinos", "Champiñones", "Hierba limón", "Galanga", "Hojas de lima kaffir"),
            Arrays.asList("Crustáceos", "Pescado"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("satay_pollo", "thai_food", "Satay de Pollo", 
            "Brochetas de pollo marinado con salsa de cacahuete.", "7.50€",
            Arrays.asList("Pechuga de pollo", "Leche de coco", "Cúrcuma", "Cacahuetes", "Azúcar de palma"),
            Arrays.asList("Cacahuetes"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));
        
        // Burger Place
        todosLosBombos.add(new Bombo("classic_burger", "burger_place", "Clásica con Queso", 
            "Ternera, cheddar, lechuga, tomate y nuestra salsa secreta.", "10.50€",
            Arrays.asList("Carne de ternera", "Queso Cheddar", "Lechuga", "Tomate", "Pan de brioche", "Salsa secreta"),
            Arrays.asList("Gluten", "Lácteos", "Huevo", "Mostaza"),
            Arrays.asList(android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_camera)));

        todosLosBombos.add(new Bombo("bbq_burger", "burger_place", "BBQ Special", 
            "Ternera con bacon crujiente, aros de cebolla y salsa barbacoa.", "11.90€",
            Arrays.asList("Carne de ternera", "Bacon", "Queso Cheddar", "Aros de cebolla", "Salsa BBQ"),
            Arrays.asList("Gluten", "Lácteos", "Soja"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("veggie_burger", "burger_place", "Veggie Delight", 
            "Hamburguesa artesana de garbanzos, espinacas y pimientos asados.", "9.50€",
            Arrays.asList("Garbanzos", "Espinacas", "Pimientos", "Cebolla roja", "Pan integral"),
            Arrays.asList("Gluten", "Sésamo"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("chicken_burger", "burger_place", "Crispy Chicken", 
            "Pollo crujiente marinado, lechuga y mayonesa suave.", "10.00€",
            Arrays.asList("Pechuga de pollo", "Rebozado crujiente", "Lechuga", "Mayonesa"),
            Arrays.asList("Gluten", "Huevo"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("double_cheese", "burger_place", "Doble Queso", 
            "Doble ración de carne y doble de queso cheddar fundido.", "13.50€",
            Arrays.asList("Doble carne de ternera", "Doble queso Cheddar", "Pepinillos", "Cebolla", "Ketchup"),
            Arrays.asList("Gluten", "Lácteos", "Mostaza"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        // Pizza Italiana
        todosLosBombos.add(new Bombo("pizza_margherita", "pizza_italiana", "Pizza Margherita", 
            "La clásica italiana con tomate San Marzano y mozzarella fresca.", "9.50€",
            Arrays.asList("Harina de trigo", "Tomate", "Mozzarella de búfala", "Albahaca fresca", "Aceite de oliva"),
            Arrays.asList("Gluten", "Lácteos"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("pizza_4_quesos", "pizza_italiana", "Cuatro Quesos", 
            "Mezcla selección de quesos italianos sobre base de tomate.", "11.50€",
            Arrays.asList("Mozzarella", "Gorgonzola", "Parmesano", "Fontina", "Tomate"),
            Arrays.asList("Gluten", "Lácteos"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("pizza_carbonara", "pizza_italiana", "Carbonara", 
            "Base de crema de nata con bacon ahumado y cebolla.", "10.50€",
            Arrays.asList("Nata", "Bacon", "Cebolla", "Mozzarella", "Huevo"),
            Arrays.asList("Gluten", "Lácteos", "Huevo"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("pizza_prosciutto", "pizza_italiana", "Prosciutto", 
            "Jamón cocido de alta calidad y champiñones laminados.", "10.00€",
            Arrays.asList("Tomate", "Mozzarella", "Jamón cocido", "Champiñones"),
            Arrays.asList("Gluten", "Lácteos"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("calzone", "pizza_italiana", "Calzone Tradicional", 
            "Pizza rellena con ricotta, salami y albahaca.", "12.00€",
            Arrays.asList("Ricotta", "Salami", "Mozzarella", "Tomate", "Pimienta"),
            Arrays.asList("Gluten", "Lácteos"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        todosLosBombos.add(new Bombo("lasagna", "pizza_italiana", "Lasagna Bolognese", 
            "Pasta fresca por capas con salsa boloñesa artesana.", "9.00€",
            Arrays.asList("Pasta fresca", "Carne picada", "Tomate", "Bechamel", "Queso rallado"),
            Arrays.asList("Gluten", "Lácteos", "Huevo"),
            Arrays.asList(android.R.drawable.ic_menu_gallery)));

        for (Bombo b : todosLosBombos) {
            if (b.getId().equals(bomboId)) {
                bomboActual = b;
                break;
            }
        }
    }
}
