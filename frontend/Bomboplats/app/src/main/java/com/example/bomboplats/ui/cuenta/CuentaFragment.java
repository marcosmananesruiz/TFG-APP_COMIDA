package com.example.bomboplats.ui.cuenta;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;
import java.io.File;

public class CuentaFragment extends Fragment {

    private ImageView ivPerfilFoto;
    private TextView tvPerfilNombre;
    private TextView tvPerfilEmail;
    private Button btnEditar;
    private Button btnDirecciones;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cuenta, container, false);

        ivPerfilFoto = view.findViewById(R.id.iv_perfil_foto);
        tvPerfilNombre = view.findViewById(R.id.tv_perfil_nombre);
        tvPerfilEmail = view.findViewById(R.id.tv_perfil_email);
        btnEditar = view.findViewById(R.id.btn_editar_perfil);
        btnDirecciones = view.findViewById(R.id.btn_direcciones);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getName().observe(getViewLifecycleOwner(), name -> tvPerfilNombre.setText(name));
        userViewModel.getEmail().observe(getViewLifecycleOwner(), email -> tvPerfilEmail.setText(email));
        
        userViewModel.getPhotoUri().observe(getViewLifecycleOwner(), uriString -> {
            if (uriString != null) {
                File file = new File(uriString);
                if (file.exists()) {
                    ivPerfilFoto.setImageURI(Uri.fromFile(file));
                } else {
                    ivPerfilFoto.setImageResource(R.drawable.mibombo);
                }
            } else {
                ivPerfilFoto.setImageResource(R.drawable.mibombo);
            }
        });

        btnEditar.setOnClickListener(v -> {
            EditarPerfilFragment fragment = new EditarPerfilFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnDirecciones.setOnClickListener(v -> {
            DireccionesCuentaFragment fragment = new DireccionesCuentaFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    public void filtrar(String texto) {
    }
}
