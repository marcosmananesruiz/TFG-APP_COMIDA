package com.example.bomboplats.ui.cuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;
import com.example.bomboplats.api.Direccion;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class NuevaDireccionFragment extends Fragment {

    private static final String ARG_DIRECCION = "direccion_json";
    private UserViewModel userViewModel;
    private TextInputEditText etPoblacion, etCalle, etCP, etPortal, etPiso;
    private Button btnGuardar;
    private TextView tvTitulo;
    private Direccion direccionAEditar;

    public static NuevaDireccionFragment newInstance(Direccion d) {
        NuevaDireccionFragment fragment = new NuevaDireccionFragment();
        Bundle args = new Bundle();
        // Usamos GSON para pasar el objeto fácilmente o podrías hacerlo Parcelable si la clase lo fuera
        args.putString(ARG_DIRECCION, new Gson().toJson(d));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nueva_direccion, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.resetUpdateAddressResult();

        tvTitulo = view.findViewById(R.id.tv_nueva_direccion_titulo);
        etPoblacion = view.findViewById(R.id.et_poblacion);
        etCalle = view.findViewById(R.id.et_calle);
        etCP = view.findViewById(R.id.et_codigo_postal);
        etPortal = view.findViewById(R.id.et_portal);
        etPiso = view.findViewById(R.id.et_piso);
        btnGuardar = view.findViewById(R.id.btn_guardar_direccion);

        if (getArguments() != null && getArguments().containsKey(ARG_DIRECCION)) {
            String json = getArguments().getString(ARG_DIRECCION);
            direccionAEditar = new Gson().fromJson(json, Direccion.class);
            prellenarCampos();
            tvTitulo.setText(R.string.editar_direccion_titulo);
        }

        userViewModel.getUpdateAddressResult().observe(getViewLifecycleOwner(), success -> {
            if (success == null) return;
            if (success) {
                Toast.makeText(getContext(),
                        direccionAEditar != null ? R.string.toast_direccion_actualizada : R.string.toast_direccion_guardada,
                        Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), R.string.error_guardar_direccion, Toast.LENGTH_SHORT).show();
            }
        });


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

            if (cp.length() != 5 || !cp.matches("\\d{5}")) {
                etCP.setError(getString(R.string.error_cp_invalido));
                return;
            }

            try {
                int portal = Integer.parseInt(portalStr);
                
                if (direccionAEditar != null) {
                    // Modo edición
                    direccionAEditar.setPoblacion(poblacion);
                    direccionAEditar.setCalle(calle);
                    direccionAEditar.setCodigoPostal(cp);
                    direccionAEditar.setPortal(portal);
                    direccionAEditar.setPiso(piso);
                    
                    userViewModel.updateAddress(direccionAEditar);

                } else {
                    // Modo creación
                    userViewModel.registerAndAssignAddress(poblacion, calle, cp, portal, piso);
                }

                //requireActivity().getSupportFragmentManager().popBackStack();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Portal debe ser un número", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void prellenarCampos() {
        if (direccionAEditar != null) {
            etPoblacion.setText(direccionAEditar.getPoblacion());
            etCalle.setText(direccionAEditar.getCalle());
            etCP.setText(direccionAEditar.getCodigoPostal());
            etPortal.setText(String.valueOf(direccionAEditar.getPortal()));
            etPiso.setText(direccionAEditar.getPiso());
        }
    }
}
