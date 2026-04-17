package com.example.bomboplats.ui.cuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;
import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.Result;

public class EditarPasswordFragment extends Fragment {

    private EditText etPassActual, etPassNueva;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editarpassword, container, false);

        etPassActual = view.findViewById(R.id.et_pass_actual);
        etPassNueva = view.findViewById(R.id.et_pass_nueva);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Observar resultado asíncrono
        userViewModel.getUpdateResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;

            if (result instanceof Result.Success) {
                Toast.makeText(getContext(), getString(R.string.toast_pass_actualizada), Toast.LENGTH_SHORT).show();
                userViewModel.resetUpdateResult();
                getParentFragmentManager().popBackStack();
            } else if (result instanceof Result.Error) {
                String errorMsg = ((Result.Error) result).getError().getMessage();
                if (LoginDataSource.ERROR_WRONG_PASSWORD.equals(errorMsg)) {
                    etPassActual.setError(getString(R.string.login_failed)); // O un string específico para pass incorrecta
                    Toast.makeText(getContext(), "La contraseña actual no es correcta", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                }
                userViewModel.resetUpdateResult();
            }
        });

        view.findViewById(R.id.btn_confirmar_pass).setOnClickListener(v -> {
            String actual = etPassActual.getText().toString();
            String nueva = etPassNueva.getText().toString();

            if (actual.isEmpty() || nueva.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.toast_rellenar_campos), Toast.LENGTH_SHORT).show();
                return;
            }

            if (nueva.length() < 5) {
                etPassNueva.setError("La contraseña debe tener al menos 5 caracteres");
                return;
            }

            // El ViewModel se encarga de verificar la contraseña actual en el servidor
            userViewModel.setPassword(actual, nueva);
        });

        return view;
    }
}
