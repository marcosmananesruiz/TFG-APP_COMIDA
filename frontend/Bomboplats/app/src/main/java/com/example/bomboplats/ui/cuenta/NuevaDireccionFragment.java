package com.example.bomboplats.ui.cuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;
import com.google.android.material.textfield.TextInputEditText;

public class NuevaDireccionFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText etPoblacion, etCalle, etCP, etPortal, etPiso;
    private Button btnGuardar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nueva_direccion, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        etPoblacion = view.findViewById(R.id.et_poblacion);
        etCalle = view.findViewById(R.id.et_calle);
        etCP = view.findViewById(R.id.et_codigo_postal);
        etPortal = view.findViewById(R.id.et_portal);
        etPiso = view.findViewById(R.id.et_piso);
        btnGuardar = view.findViewById(R.id.btn_guardar_direccion);

        btnGuardar.setOnClickListener(v -> {
            String poblacion = etPoblacion.getText().toString().trim();
            String calle = etCalle.getText().toString().trim();
            String cp = etCP.getText().toString().trim();
            String portalStr = etPortal.getText().toString().trim();
            String piso = etPiso.getText().toString().trim();

            if (poblacion.isEmpty() || calle.isEmpty() || cp.isEmpty() || portalStr.isEmpty()) {
                Toast.makeText(getContext(), R.string.toast_campos_obligatorios, Toast.LENGTH_SHORT).show();
                return;
            }

            // Validación de Código Postal (exactamente 5 cifras)
            if (cp.length() != 5 || !cp.matches("\\d{5}")) {
                etCP.setError(getString(R.string.error_cp_invalido));
                return;
            }

            try {
                int portal = Integer.parseInt(portalStr);
                userViewModel.registerAndAssignAddress(poblacion, calle, cp, portal, piso);
                Toast.makeText(getContext(), R.string.toast_direccion_guardada, Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Portal debe ser un número", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
