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
import com.example.bomboplats.R;
import com.example.bomboplats.ui.carrito.CarritoViewModel;

public class DetalleBomboFragment extends Fragment {

    private CarritoViewModel carritoViewModel;
    private String bomboId;
    private String nombre;
    private String precio;
    private String desc;
    private int cantidad = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_bombo, container, false);

        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);

        if (getArguments() != null) {
            bomboId = getArguments().getString("bomboId");
            nombre = getArguments().getString("nombre");
            precio = getArguments().getString("precio");
            desc = getArguments().getString("desc");
        }
        
        ImageView img = view.findViewById(R.id.img_detalle_bombo);
        TextView tvNombre = view.findViewById(R.id.tv_nombre_detalle);
        TextView tvPrecio = view.findViewById(R.id.tv_precio_detalle);
        TextView tvDesc = view.findViewById(R.id.tv_descripcion_detalle);
        TextView tvCantidad = view.findViewById(R.id.tv_cantidad);
        Button btnMenos = view.findViewById(R.id.btn_menos);
        Button btnMas = view.findViewById(R.id.btn_mas);
        Button btnPedido = view.findViewById(R.id.btn_realizar_pedido);

        tvNombre.setText(nombre);
        tvPrecio.setText(precio);
        tvDesc.setText(desc);

        int resID = getContext().getResources().getIdentifier(bomboId, "drawable", getContext().getPackageName());
        if (resID != 0) img.setImageResource(resID);

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
            if (bomboId != null) {
                carritoViewModel.agregarAlCarrito(bomboId, cantidad);
                Toast.makeText(getContext(), "¡" + cantidad + " x " + nombre + " añadido al carrito!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
