package com.example.bomboplats.ui.cuenta;

import android.os.Bundle;
import android.util.Patterns;
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
import com.example.bomboplats.data.LoginDataSource;
import com.example.bomboplats.data.Result;

public class EditarMailFragment extends Fragment {

    private EditText etMailNuevo;
    private Button btnConfirmar;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editarmail, container, false);

        etMailNuevo = view.findViewById(R.id.et_mail_nuevo);
        btnConfirmar = view.findViewById(R.id.btn_confirmar_mail);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        btnConfirmar.setOnClickListener(v -> {
            String nuevo = etMailNuevo.getText().toString().trim();

            if (nuevo.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.toast_rellenar_campos), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(nuevo).matches()) {
                etMailNuevo.setError(getString(R.string.error_mail_invalido));
                return;
            }

            String mailActual = userViewModel.getEmail().getValue();
            if (nuevo.equalsIgnoreCase(mailActual)) {
                Toast.makeText(getContext(), getString(R.string.error_mail_igual_al_actual), Toast.LENGTH_SHORT).show();
                return;
            }

            Result<?> result = userViewModel.setEmail(nuevo);
            if (result instanceof Result.Success) {
                Toast.makeText(getContext(), getString(R.string.toast_correo_actualizado), Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else if (result instanceof Result.Error) {
                String errorMsg = ((Result.Error) result).getError().getMessage();
                if (LoginDataSource.ERROR_EMAIL_ALREADY_EXISTS.equals(errorMsg)) {
                    Toast.makeText(getContext(), getString(R.string.error_email_exists), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_actualizar_correo), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
