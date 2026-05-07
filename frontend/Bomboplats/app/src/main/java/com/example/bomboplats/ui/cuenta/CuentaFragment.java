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
import com.bumptech.glide.signature.ObjectKey;
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

        // Refrescamos los datos al entrar para asegurar información actualizada de la base de datos
        userViewModel.refreshUserData();

        userViewModel.getName().observe(getViewLifecycleOwner(), name -> {
            if (name == null || name.isEmpty()) {
                tvPerfilNombre.setText(R.string.default_nombre_usuario);
            } else {
                tvPerfilNombre.setText(name);
            }
        });

        userViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            if (email == null || email.isEmpty()) {
                tvPerfilEmail.setText(R.string.default_email_usuario);
            } else {
                tvPerfilEmail.setText(email);
            }
        });
        
        userViewModel.getPhotoUri().observe(getViewLifecycleOwner(), uriString -> {
            if (uriString != null && !uriString.isEmpty()) {
                // Usamos una firma (signature) basada en el tiempo para invalidar la caché de Glide
                // y que se muestre la foto nueva inmediatamente después del cambio.
                ObjectKey signature = new ObjectKey(System.currentTimeMillis());

                if (uriString.startsWith("http")) {
                    Glide.with(this)
                            .load(uriString)
                            .signature(signature)
                            .placeholder(R.drawable.ic_user_default)
                            .error(R.drawable.ic_user_default)
                            .circleCrop()
                            .into(ivPerfilFoto);
                } else {
                    File file = new File(uriString);
                    if (file.exists()) {
                        Glide.with(this)
                                .load(file)
                                .signature(signature)
                                .placeholder(R.drawable.ic_user_default)
                                .error(R.drawable.ic_user_default)
                                .circleCrop()
                                .into(ivPerfilFoto);
                    } else {
                        Glide.with(this)
                                .load(BASE_BUCKET + uriString)
                                .signature(signature)
                                .placeholder(R.drawable.ic_user_default)
                                .error(R.drawable.ic_user_default)
                                .circleCrop()
                                .into(ivPerfilFoto);
                    }
                }
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_user_default)
                        .circleCrop()
                        .into(ivPerfilFoto);
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
