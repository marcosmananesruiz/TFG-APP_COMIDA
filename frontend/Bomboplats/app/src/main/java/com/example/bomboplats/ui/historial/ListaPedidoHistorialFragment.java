package com.example.bomboplats.ui.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.BomboConCantidad;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import com.example.bomboplats.ui.general.BomboAdapter;
import com.example.bomboplats.ui.general.DetalleBomboFragment;
import java.util.ArrayList;
import java.util.List;

public class ListaPedidoHistorialFragment extends Fragment implements BomboAdapter.OnBomboClickListener {

    private TextView tvId, tvFecha;
    private RecyclerView recyclerView;
    private BomboAdapter adapter;
    private Pedido pedido;
    private UserViewModel userViewModel;
    private CarritoViewModel carritoViewModel;

    public static ListaPedidoHistorialFragment newInstance(Pedido pedido) {
        ListaPedidoHistorialFragment fragment = new ListaPedidoHistorialFragment();
        Bundle args = new Bundle();
        args.putSerializable("pedido", pedido);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listapedidohistorial, container, false);

        if (getArguments() != null) {
            pedido = (Pedido) getArguments().getSerializable("pedido");
        }

        tvId = view.findViewById(R.id.tv_historial_id);
        tvFecha = view.findViewById(R.id.tv_historial_fecha);
        recyclerView = view.findViewById(R.id.rv_productos_historial);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);

        if (pedido != null) {
            tvId.setText("Pedido #" + pedido.getId());
            tvFecha.setText(pedido.getFecha());

            List<Bombo> bombos = new ArrayList<>();
            for (BomboConCantidad item : pedido.getItems()) {
                bombos.add(item.getBombo());
            }

            adapter = new BomboAdapter(bombos, this, userViewModel);
            recyclerView.setAdapter(adapter);
        }

        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), favs -> {
            if (adapter != null) adapter.notifyDataSetChanged();
        });

        return view;
    }

    @Override
    public void onBomboClick(Bombo b) {
        DetalleBomboFragment fragment = new DetalleBomboFragment();
        Bundle args = new Bundle();
        args.putString("bomboId", b.getId());
        args.putString("nombre", b.getNombre());
        args.putString("precio", b.getPrecio());
        args.putString("desc", b.getDescripcion());
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFavoritoClick(Bombo b) {
        userViewModel.toggleFavorito(b.getId());
    }

    @Override
    public void onAgregarCarritoClick(Bombo b) {
        carritoViewModel.agregarAlCarrito(b.getId(), 1);
        Toast.makeText(getContext(), "¡" + b.getNombre() + " añadido al carrito!", Toast.LENGTH_SHORT).show();
    }
}
