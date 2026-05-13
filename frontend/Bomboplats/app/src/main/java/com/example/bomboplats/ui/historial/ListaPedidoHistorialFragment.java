package com.example.bomboplats.ui.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.example.bomboplats.data.model.StagedBombo;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import com.example.bomboplats.ui.general.DetalleBomboFragment;
import java.util.ArrayList;
import java.util.List;

public class ListaPedidoHistorialFragment extends Fragment implements HistorialDetalleAdapter.OnItemClickListener {

    private TextView tvId, tvFecha;
    private RecyclerView recyclerView;
    private HistorialDetalleAdapter adapter;
    private Pedido pedido;
    private UserViewModel userViewModel;
    private FoodRepository foodRepository;

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
        foodRepository = FoodRepository.getInstance(requireContext());

        if (pedido != null) {
            String prefixId = getString(R.string.prefix_pedido_id);
            tvId.setText(prefixId + pedido.getId());
            tvFecha.setText(pedido.getFecha());

            List<StagedBombo> itemsHistorial = new ArrayList<>();
            if (pedido.getItems() != null) {
                for (PedidoItem item : pedido.getItems()) {
                    String itemKey = item.getRestauranteId() + ":" + item.getBomboId();
                    Bombo b = foodRepository.getBomboPorId(itemKey);
                    if (b != null) {
                        itemsHistorial.add(new StagedBombo(b, item.getCantidad(), item.getModificaciones()));
                    }
                }
            }

            adapter = new HistorialDetalleAdapter(itemsHistorial, this, userViewModel);
            recyclerView.setAdapter(adapter);
        }

        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), favs -> {
            if (adapter != null) adapter.notifyDataSetChanged();
        });

        return view;
    }

    @Override
    public void onBomboClick(StagedBombo stagedBombo) {
        Bombo b = stagedBombo.getBombo();
        DetalleBomboFragment fragment = new DetalleBomboFragment();
        Bundle args = new Bundle();
        args.putString("bomboId", b.getId());
        
        // Pasamos los datos del pedido para el modo lectura
        args.putBoolean("modoLectura", true);
        args.putInt("cantidad", stagedBombo.getCantidad());
        args.putStringArrayList("modificaciones", new ArrayList<>(stagedBombo.getModificaciones()));

        fragment.setArguments(args);

        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onFavoritoClick(Bombo b) {
        userViewModel.toggleFavorito(b);
    }
}
