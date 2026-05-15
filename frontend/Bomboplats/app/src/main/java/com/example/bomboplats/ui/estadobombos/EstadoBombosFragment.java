package com.example.bomboplats.ui.estadobombos;

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
import com.example.bomboplats.data.model.EstadoPedido;
import com.example.bomboplats.ui.historial.ListaPedidoHistorialFragment;

import java.util.List;

public class EstadoBombosFragment extends Fragment implements EstadoBombosAdapter.OnEstadoPedidoClickListener {

    private RecyclerView recyclerView;
    private EstadoBombosAdapter adapter;
    private EstadoBombosViewModel viewModel;
    private TextView tvVacio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadobombos, container, false);

        recyclerView = view.findViewById(R.id.rv_estado_bombos);
        tvVacio = view.findViewById(R.id.tv_estado_vacio);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EstadoBombosAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(EstadoBombosViewModel.class);

        List<EstadoPedido> pedidos = this.viewModel.getPedidosEnEstado(getContext());
        if (pedidos == null || pedidos.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvVacio.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvVacio.setVisibility(View.GONE);
            adapter.setLista(pedidos);
        }

        return view;
    }

    @Override
    public void onPedidoClick(EstadoPedido estadoPedido) {
        ListaPedidoHistorialFragment fragment = ListaPedidoHistorialFragment.newInstance(estadoPedido.getPedido());
        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
        }
    }

    public void filtrar(String texto) {}
}
