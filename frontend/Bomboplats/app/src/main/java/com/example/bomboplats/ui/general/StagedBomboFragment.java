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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bomboplats.R;
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.StagedBombo;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.cuenta.UserViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento para mostrar los detalles de un plato.
 */
public class StagedBomboFragment extends Fragment {

    private CarritoViewModel carritoViewModel;
    private UserViewModel userViewModel;
    private TextView tvNombre, tvPrecio, tvDescription, tvModificaciones, tvCantidad;
    private ImageView ivFavorito;
    private RecyclerView rvFotos;
    private StagedBombo stagedBombo;
    private Bombo bomboActual;
    private int cantidad = 1;

    private static final String DEFAULT_BOMBO_IMAGE = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/platos/default.jpg";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staged_bombo_view, container, false);

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);


        tvNombre = view.findViewById(R.id.tv_bombo_nombre);
        tvPrecio = view.findViewById(R.id.tv_bombo_precio);
        tvDescription = view.findViewById(R.id.tv_bombo_descripcion);
        rvFotos = view.findViewById(R.id.rv_bombo_fotos);
        tvCantidad = view.findViewById(R.id.tv_cantidad);
        ivFavorito = view.findViewById(R.id.iv_favorito);
        tvModificaciones = view.findViewById(R.id.tv_ingredientes);

        View btnMenos = view.findViewById(R.id.btn_menos);
        View btnMas = view.findViewById(R.id.btn_mas);
        Button btnEliminar = view.findViewById(R.id.btn_realizar_pedido);

        if (getArguments() != null) {
            int id = getArguments().getInt("staged_bombo");
            this.stagedBombo = this.carritoViewModel.findStagedBomboById(id);
            this.bomboActual = this.stagedBombo.getBombo();
            this.cantidad = this.stagedBombo.getCantidad();
            this.tvCantidad.setText(String.valueOf(this.cantidad));
        }

        // Listener para aumentar la cantidad de un plato
        btnMas.setOnClickListener(v -> {
            cantidad++;
            tvCantidad.setText(String.valueOf(cantidad));
            this.stagedBombo.setCantidad(cantidad);
        });

        // Listener para disminuir la cantidad de un plato
        btnMenos.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                tvCantidad.setText(String.valueOf(cantidad));
                this.stagedBombo.setCantidad(cantidad);
            }
        });

        // Listener para añadir o eliminar un pato de favoritos
        ivFavorito.setOnClickListener(v -> {
            if (bomboActual != null) {
                userViewModel.toggleFavorito(bomboActual);
                actualizarIconoFavorito();
                boolean esFavoritoNow = userViewModel.esFavorito(bomboActual.getId());
                int resId = esFavoritoNow ? R.string.toast_fav_add : R.string.toast_fav_rem;
                Toast.makeText(getContext(), getString(resId), Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para eliminar un plato del carrito
        btnEliminar.setOnClickListener(v -> {
            this.carritoViewModel.removerDelCarrito(this.stagedBombo);
            Toast.makeText(getContext(), getString(R.string.bombo_eliminado_del_carro), Toast.LENGTH_SHORT).show();
        });

        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), ids -> actualizarIconoFavorito());
        mostrarInfoBombo();
        return view;
    }

    // Cambia el icono de favorito
    private void actualizarIconoFavorito() {
        if (bomboActual != null && ivFavorito != null && userViewModel != null) {
            boolean esFav = userViewModel.esFavorito(bomboActual.getId());
            ivFavorito.setImageResource(esFav ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        }
    }

    // Muestra la información básica del bombo como el nombre, el precio y la descripción
    private void mostrarInfoBombo() {
        if (bomboActual != null) {
            tvNombre.setText(bomboActual.getNombre());

            // Formatear el precio
            String precio = bomboActual.getPrecio();
            if (precio != null && !precio.contains("€")) {
                precio += "€";
            }
            tvPrecio.setText(precio);

            tvDescription.setText(bomboActual.getDescripcion());

            // Mostrar las fotos
            List<String> fotos = bomboActual.getFotos();
            FotoCarruselAdapter adapter = new FotoCarruselAdapter(fotos, DEFAULT_BOMBO_IMAGE);
            rvFotos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvFotos.setAdapter(adapter);

            // Mostrar las modificaciones seleccionadas
            String modficaciones = this.stagedBombo.getModificaciones().stream()
                    .map(m -> "- " + m + "\n")
                    .reduce((m1, m2) -> m1+m2)
                    .orElse("No hay ninguna modificación seleccionada.");
            this.tvModificaciones.setText(modficaciones);
        }
    }
}
