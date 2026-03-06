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
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import java.util.ArrayList;

public class HistorialFragment extends Fragment implements HistorialAdapter.OnPedidoClickListener {

    private RecyclerView recyclerView;
    private HistorialAdapter adapter;
    private HistorialViewModel historialViewModel;
    private TextView tvVacio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        recyclerView = view.findViewById(R.id.rv_historial);
        tvVacio = view.findViewById(R.id.tv_historial_vacio);

        historialViewModel = new ViewModelProvider(requireActivity()).get(HistorialViewModel.class);

        adapter = new HistorialAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        historialViewModel.getPedidos().observe(getViewLifecycleOwner(), pedidos -> {
            if (pedidos == null || pedidos.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvVacio.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvVacio.setVisibility(View.GONE);
                adapter.setPedidos(pedidos);
            }
        });

        return view;
    }

    @Override
    public void onProductosClick(Pedido pedido) {
        ListaPedidoHistorialFragment fragment = ListaPedidoHistorialFragment.newInstance(pedido);
        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
        }
    }

    public void filtrar(String texto) {
        // Opcional: implementar filtrado por ID o fecha
    }
}
