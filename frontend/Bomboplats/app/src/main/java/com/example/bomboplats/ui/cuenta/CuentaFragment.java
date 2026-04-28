package com.example.bomboplats.ui.cuenta;

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
import com.bumptech.glide.Glide;
import com.example.bomboplats.R;
import java.io.File;

public class CuentaFragment extends Fragment {

    private ImageView ivPerfilFoto;
    private TextView tvPerfilNombre;
    private TextView tvPerfilEmail;
    private Button btnEditar;
    private Button btnDirecciones;
    private UserViewModel userViewModel;

    private static final String BASE_BUCKET = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/";
    private static final String DEFAULT_USER_IMAGE = BASE_BUCKET + "profile/default.jpg";

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
            String fotoUrl = DEFAULT_USER_IMAGE;
            if (uriString != null && !uriString.isEmpty()) {
                if (uriString.startsWith("http")) {
                    fotoUrl = uriString;
                } else {
                    // Si es una ruta local o solo el nombre del archivo
                    File file = new File(uriString);
                    if (file.exists()) {
                        Glide.with(this)
                                .load(file)
                                .placeholder(R.drawable.mibombo)
                                .error(DEFAULT_USER_IMAGE)
                                .circleCrop()
                                .into(ivPerfilFoto);
                        return;
                    } else {
                        fotoUrl = BASE_BUCKET + uriString;
                    }
                }
            }

            Glide.with(this)
                    .load(fotoUrl)
                    .placeholder(R.drawable.mibombo)
                    .error(DEFAULT_USER_IMAGE)
                    .circleCrop()
                    .into(ivPerfilFoto);
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
