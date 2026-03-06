package com.example.bomboplats.ui.estadobombos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.utils.NotificationHelper;

public class EstadoBombosFragment extends Fragment {

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
        Button btnSimular = view.findViewById(R.id.btn_simular_cambio);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EstadoBombosAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(EstadoBombosViewModel.class);

        // Observar la lista de pedidos
        viewModel.getBombosEnEstado().observe(getViewLifecycleOwner(), bombos -> {
            if (bombos == null || bombos.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvVacio.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvVacio.setVisibility(View.GONE);
                adapter.setLista(bombos);
            }
        });

        // El observer de notificaciones se moverá a la GeneralActivity

        btnSimular.setOnClickListener(v -> {
            // Este botón ahora solo servirá para forzar una actualización de estados
            // si el temporizador no ha saltado aún.
        });

        return view;
    }

    // Método añadido para que la búsqueda global no falle
    public void filtrar(String texto) {
        // En esta pantalla, la búsqueda no hace nada, pero el método debe existir
    }
}
