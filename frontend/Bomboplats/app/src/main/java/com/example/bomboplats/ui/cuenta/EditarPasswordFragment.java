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

        view.findViewById(R.id.btn_confirmar_pass).setOnClickListener(v -> {
            String actual = etPassActual.getText().toString();
            String nueva = etPassNueva.getText().toString();

            if (actual.isEmpty() || nueva.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.toast_rellenar_campos), Toast.LENGTH_SHORT).show();
            } else {
                // El método correcto es setPassword(old, new)
                userViewModel.setPassword(actual, nueva);
                Toast.makeText(getContext(), getString(R.string.toast_pass_actualizada), Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
