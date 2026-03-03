package com.example.bomboplats.ui.misbombos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bomboplats.R;

public class MisBombosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_misbombos, container, false);
    }

    public void filtrar(String texto) {
        // Lógica de filtrado para Mis Bombos cuando tengas la lista
    }
}
