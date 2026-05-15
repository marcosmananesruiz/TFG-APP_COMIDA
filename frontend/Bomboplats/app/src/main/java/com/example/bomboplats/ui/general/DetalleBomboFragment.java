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

import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import com.example.bomboplats.utils.BomboUtils;

import java.util.ArrayList;
import java.util.List;

public class DetalleBomboFragment extends Fragment implements ModificacionesAdapter.OnModificacionClickListener {

    private CarritoViewModel carritoViewModel;
    private UserViewModel userViewModel;
    private FoodRepository foodRepository;
    
    private TextView tvNombre, tvPrecio, tvDescription, tvCantidad, tvModificacionesResumen, tvLabelIngredientes;
    private ImageView ivFavorito;
    private RecyclerView rvFotos, rvModificaciones;
    private View layoutCantidad, btnMas, btnMenos;
    private Button btnPedido;

    private String bomboId;
    private Bombo bomboActual;
    private final List<String> selectedModifications = new ArrayList<>();
    private int cantidad = 1;

    private boolean modoLectura = false;
    private List<String> modificacionesHistorial;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bomboId = getArguments().getString("bomboId");
            modoLectura = getArguments().getBoolean("modoLectura", false);
            cantidad = getArguments().getInt("cantidad", 1);
            modificacionesHistorial = getArguments().getStringArrayList("modificaciones");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_bombo, container, false);
        
        initViews(view);
        initViewModels();

        if (bomboId != null) {
            bomboActual = foodRepository.getBomboPorId(bomboId);
            if (bomboActual != null) {
                mostrarInfoBomboBasica();
                setupUIByMode();
            }
        }

        setupFavoritoLogic();
        return view;
    }

    private void initViews(View view) {
        tvNombre = view.findViewById(R.id.tv_bombo_nombre);
        tvPrecio = view.findViewById(R.id.tv_bombo_precio);
        tvDescription = view.findViewById(R.id.tv_bombo_descripcion);
        rvFotos = view.findViewById(R.id.rv_bombo_fotos);
        tvCantidad = view.findViewById(R.id.tv_cantidad);
        ivFavorito = view.findViewById(R.id.iv_favorito);
        rvModificaciones = view.findViewById(R.id.rv_ingredientes);
        tvModificacionesResumen = view.findViewById(R.id.tv_modificaciones_resumen);
        tvLabelIngredientes = view.findViewById(R.id.label_ingredientes);
        layoutCantidad = view.findViewById(R.id.layout_cantidad);
        btnMas = view.findViewById(R.id.btn_mas);
        btnMenos = view.findViewById(R.id.btn_menos);
        btnPedido = view.findViewById(R.id.btn_realizar_pedido);
    }

    private void initViewModels() {
        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        foodRepository = FoodRepository.getInstance(requireContext());
    }

    private void setupUIByMode() {
        tvCantidad.setText(String.valueOf(cantidad));

        if (modoLectura) {
            layoutCantidad.setVisibility(View.GONE);
            btnPedido.setVisibility(View.GONE);
            rvModificaciones.setVisibility(View.GONE);
            
            if (tvLabelIngredientes != null) tvLabelIngredientes.setText(R.string.label_modificaciones_pedido);
            
            if (modificacionesHistorial != null && !modificacionesHistorial.isEmpty()) {
                tvModificacionesResumen.setText(BomboUtils.formatModificaciones(modificacionesHistorial));
            } else {
                tvModificacionesResumen.setText(getString(R.string.no_especificados));
            }
            tvModificacionesResumen.setVisibility(View.VISIBLE);
        } else {
            tvModificacionesResumen.setVisibility(View.GONE);
            rvModificaciones.setVisibility(View.VISIBLE);
            
            rvModificaciones.setAdapter(new ModificacionesAdapter(bomboActual.getIngredientes(), this));

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

            btnPedido.setOnClickListener(v -> {
                carritoViewModel.agregarAlCarrito(bomboActual, cantidad, selectedModifications);
                Toast.makeText(getContext(), getString(R.string.carrito_item_added, cantidad, bomboActual.getNombre()), Toast.LENGTH_SHORT).show();

                if (getActivity() instanceof GeneralActivity generalActivity) {
                    generalActivity.updateCartIcon(this);
                }

            });
        }
    }

    private void mostrarInfoBomboBasica() {
        tvNombre.setText(bomboActual.getNombre());
        tvPrecio.setText(BomboUtils.formatPrecio(bomboActual.getPrecio()));
        tvDescription.setText(bomboActual.getDescripcion());

        rvFotos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFotos.setAdapter(new FotoCarruselAdapter(bomboActual.getFotos(), BomboUtils.DEFAULT_BOMBO_IMAGE));
    }

    private void setupFavoritoLogic() {
        actualizarIconoFavorito();
        ivFavorito.setOnClickListener(v -> {
            if (bomboActual != null) {
                userViewModel.toggleFavorito(bomboActual);
                actualizarIconoFavorito();
                boolean esFav = userViewModel.esFavorito(bomboActual.getId());
                Toast.makeText(getContext(), getString(esFav ? R.string.toast_fav_add : R.string.toast_fav_rem), Toast.LENGTH_SHORT).show();
            }
        });
        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), ids -> actualizarIconoFavorito());
    }

    private void actualizarIconoFavorito() {
        if (bomboActual != null && ivFavorito != null) {
            boolean esFav = userViewModel.esFavorito(bomboActual.getId());
            ivFavorito.setImageResource(esFav ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        }
    }

    @Override
    public void onModificacionClick(String modificacion, boolean isChecked) {
        if (modoLectura) return;
        if (isChecked) selectedModifications.add(modificacion);
        else selectedModifications.remove(modificacion);
    }
}
