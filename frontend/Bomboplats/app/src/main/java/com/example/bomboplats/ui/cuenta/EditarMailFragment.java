package com.example.bomboplats.ui.cuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;

public class EditarMailFragment extends Fragment {

    private EditText etMailActual, etMailNuevo;
    private Button btnConfirmar;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editarmail, container, false);

        etMailActual = view.findViewById(R.id.et_mail_actual);
        etMailNuevo = view.findViewById(R.id.et_mail_nuevo);
        btnConfirmar = view.findViewById(R.id.btn_confirmar_mail);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        btnConfirmar.setOnClickListener(v -> {
            String actual = etMailActual.getText().toString().trim();
            String nuevo = etMailNuevo.getText().toString().trim();

            if (actual.isEmpty() || nuevo.isEmpty()) {
                Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String mailGuardado = userViewModel.getEmail().getValue();
            if (actual.equals(mailGuardado)) {
                userViewModel.setEmail(nuevo);
                Toast.makeText(getContext(), "Correo actualizado con éxito", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                etMailActual.setError("El correo actual no coincide");
            }
        });

        return view;
    }
}
