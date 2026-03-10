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

public class EditarPasswordFragment extends Fragment {

    private EditText etPassActual, etPassNueva;
    private Button btnConfirmar;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editarpassword, container, false);

        etPassActual = view.findViewById(R.id.et_pass_actual);
        etPassNueva = view.findViewById(R.id.et_pass_nueva);
        btnConfirmar = view.findViewById(R.id.btn_confirmar_pass);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        btnConfirmar.setOnClickListener(v -> {
            String actual = etPassActual.getText().toString().trim();
            String nueva = etPassNueva.getText().toString().trim();

            if (actual.isEmpty() || nueva.isEmpty()) {
                Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nueva.length() < 6) {
                etPassNueva.setError("La nueva contraseña debe tener al menos 6 caracteres");
                return;
            }

            String passGuardada = userViewModel.getPassword().getValue();
            if (actual.equals(passGuardada)) {
                userViewModel.setPassword(nueva);
                Toast.makeText(getContext(), "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                etPassActual.setError("La contraseña actual no es correcta");
            }
        });

        return view;
    }
}
