package com.example.bomboplats.ui.general;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import com.example.bomboplats.ui.misbombos.FavoritosViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetalleBomboFragment extends Fragment {

    private CarritoViewModel carritoViewModel;
    private UserViewModel userViewModel;
    private TextView tvNombre, tvPrecio, tvDescripcion, tvIngredientes, tvAlergenos, tvCantidad;
    private ImageView ivFavorito;
    private RecyclerView rvFotos;
    private String bomboId;
    private Bombo bomboActual;
    private int cantidad = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_bombo, container, false);

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        tvNombre = view.findViewById(R.id.tv_bombo_nombre);
        tvPrecio = view.findViewById(R.id.tv_bombo_precio);
        tvDescripcion = view.findViewById(R.id.tv_bombo_descripcion);
        tvIngredientes = view.findViewById(R.id.tv_bombo_ingredientes);
        tvAlergenos = view.findViewById(R.id.tv_bombo_alergenos);
        rvFotos = view.findViewById(R.id.rv_bombo_fotos);
        tvCantidad = view.findViewById(R.id.tv_cantidad);
        ivFavorito = view.findViewById(R.id.iv_favorito);
        
        View btnMenos = view.findViewById(R.id.btn_menos);
        View btnMas = view.findViewById(R.id.btn_mas);
        Button btnPedido = view.findViewById(R.id.btn_realizar_pedido);

        if (getArguments() != null) {
            bomboId = getArguments().getString("bomboId");
        }

        cargarDatosEjemplo();
        mostrarInfoBombo();

        // Inicializar estado de favorito
        actualizarIconoFavorito();

        btnMas.setOnClickListener(v -> {
            cantidad++;
            tvCantidad.setText(String.valueOf(cantidad));
        });

        btnMenos.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                tvCantidad.setText(String.valueOf(cantidad));
            }
        });

        ivFavorito.setOnClickListener(v -> {
            if (bomboId != null) {
                userViewModel.toggleFavorito(bomboId);
                boolean esFavorito = userViewModel.esFavorito(bomboId);
                actualizarIconoFavorito();
                String mensaje = esFavorito ? "Añadido a favoritos" : "Eliminado de favoritos";
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        btnPedido.setOnClickListener(v -> {
            if (bomboActual != null) {
                carritoViewModel.agregarAlCarrito(bomboActual.getId(), cantidad);
                Toast.makeText(getContext(), "¡" + cantidad + " x " + bomboActual.getNombre() + " añadido al carrito!", Toast.LENGTH_SHORT).show();
            }
        });

        // Observar cambios en favoritos para mantener UI sincronizada
        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), ids -> {
            actualizarIconoFavorito();
        });

        return view;
    }

    private void actualizarIconoFavorito() {
        if (bomboId != null && ivFavorito != null && userViewModel != null) {
            boolean esFav = userViewModel.esFavorito(bomboId);
            ivFavorito.setImageResource(esFav ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        }
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
        
        todosLosBombos.add(new Bombo("classic_burger", "burger_place", "Clásica con Queso", 
            "Ternera, cheddar, lechuga, tomate y nuestra salsa secreta.", "10.50€",
            Arrays.asList("Carne de ternera", "Queso Cheddar", "Lechuga", "Tomate", "Pan de brioche", "Salsa secreta"),
            Arrays.asList("Gluten", "Lácteos", "Huevo", "Mostaza"),
            Arrays.asList(android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_camera)));

        for (Bombo b : todosLosBombos) {
            if (b.getId().equals(bomboId)) {
                bomboActual = b;
                break;
            }
        }
        
        if (bomboActual == null) {
            bomboActual = new Bombo(bomboId, "desconocido", "Plato de Ejemplo", 
                "Descripción detallada del plato.", "9.99€",
                Arrays.asList("Ingrediente A", "Ingrediente B"),
                Arrays.asList("Alérgeno X"),
                Arrays.asList(android.R.drawable.ic_menu_gallery));
        }
    }
}
