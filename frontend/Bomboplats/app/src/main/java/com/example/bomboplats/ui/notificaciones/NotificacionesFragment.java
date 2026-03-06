package com.example.bomboplats.ui.notificaciones;

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
import com.example.bomboplats.R;
import java.util.ArrayList;

public class NotificacionesFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificacionesAdapter adapter;
    private NotificacionesViewModel viewModel;
    private TextView tvNoNotis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        recyclerView = view.findViewById(R.id.rv_notificaciones);
        tvNoNotis = view.findViewById(R.id.tv_no_notis);

        viewModel = new ViewModelProvider(requireActivity()).get(NotificacionesViewModel.class);

        adapter = new NotificacionesAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel.getListaNotificaciones().observe(getViewLifecycleOwner(), notificaciones -> {
            if (notificaciones == null || notificaciones.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvNoNotis.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvNoNotis.setVisibility(View.GONE);
                adapter.setLista(notificaciones);
            }
        });

        return view;
    }

    public void filtrar(String texto) {
        // Lógica de filtrado opcional para notificaciones
    }
}
