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
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import java.util.ArrayList;
import java.util.List;

public class DetalleBomboFragment extends Fragment {

    private CarritoViewModel carritoViewModel;
    private UserViewModel userViewModel;
    private FoodRepository foodRepository;
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
        foodRepository = FoodRepository.getInstance(requireContext());

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

        if (bomboId != null) {
            bomboActual = foodRepository.getBomboPorId(bomboId);
        }
        
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
            if (bomboActual != null) {
                userViewModel.toggleFavorito(bomboActual.getRestauranteId(), bomboActual.getId());
                actualizarIconoFavorito();
                boolean esFavoritoNow = userViewModel.esFavorito(bomboActual.getRestauranteId(), bomboActual.getId());
                String mensaje = esFavoritoNow ? "Añadido a favoritos" : "Eliminado de favoritos";
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        btnPedido.setOnClickListener(v -> {
            if (bomboActual != null) {
                // Formato compuesto "restauranteId:bomboId" para evitar conflictos
                String itemKey = bomboActual.getRestauranteId() + ":" + bomboActual.getId();
                carritoViewModel.agregarAlCarrito(itemKey, cantidad);
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
        if (bomboActual != null && ivFavorito != null && userViewModel != null) {
            boolean esFav = userViewModel.esFavorito(bomboActual.getRestauranteId(), bomboActual.getId());
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

            if (bomboActual.getFotos() != null && !bomboActual.getFotos().isEmpty()) {
                List<String> fotosString = bomboActual.getFotos();
                List<Integer> resIds = new ArrayList<>();
                for (String fotoName : fotosString) {
                    int resId = getContext().getResources().getIdentifier(fotoName, "drawable", getContext().getPackageName());
                    if (resId != 0) resIds.add(resId);
                }
                if (resIds.isEmpty()) resIds.add(R.drawable.ic_launcher_background);
                
                RestauranteFotoAdapter fotoAdapter = new RestauranteFotoAdapter(resIds);
                rvFotos.setAdapter(fotoAdapter);
            }
        }
    }
}
