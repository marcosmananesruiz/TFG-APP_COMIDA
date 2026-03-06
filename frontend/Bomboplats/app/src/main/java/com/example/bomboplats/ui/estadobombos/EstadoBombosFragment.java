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
import com.example.bomboplats.R;
import java.util.ArrayList;

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
        
        if (recyclerView == null) {
            // Si el layout no tenía el ID correcto, lo manejamos o asumimos que lo actualizaremos ahora
            return view; 
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EstadoBombosAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(EstadoBombosViewModel.class);

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

        return view;
    }

    public void filtrar(String texto) {
        // Lógica de filtrado opcional
    }
}
