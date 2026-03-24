package com.example.bomboplats.ui.cuenta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.bomboplats.R;
import com.example.bomboplats.ui.login.LoginActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class EditarPerfilFragment extends Fragment {

    private EditText etNombre;
    private ImageView ivFoto;
    private Button btnGuardar, btnEditarEmail, btnEditarPassword, btnEliminarCuenta;
    private UserViewModel userViewModel;
    private Uri pendingPhotoUri;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    if (isImageSizeValid(uri)) {
                        pendingPhotoUri = uri;
                        ivFoto.setImageURI(uri);
                        Toast.makeText(getContext(), getString(R.string.toast_foto_seleccionada), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_image_too_large), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editarperfil, container, false);

        etNombre = view.findViewById(R.id.et_editar_nombre);
        ivFoto = view.findViewById(R.id.iv_editar_foto);
        btnGuardar = view.findViewById(R.id.btn_guardar_perfil);
        btnEditarEmail = view.findViewById(R.id.btn_ir_editar_email);
        btnEditarPassword = view.findViewById(R.id.btn_ir_editar_password);
        btnEliminarCuenta = view.findViewById(R.id.btn_eliminar_cuenta);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Cargar nombre actual
        userViewModel.getName().observe(getViewLifecycleOwner(), name -> {
            if (etNombre.getText().toString().isEmpty()) {
                etNombre.setText(name);
            }
        });

        // Cargar foto actual
        userViewModel.getPhotoUri().observe(getViewLifecycleOwner(), uriString -> {
            if (uriString != null && pendingPhotoUri == null) {
                File file = new File(uriString);
                if (file.exists()) {
                    ivFoto.setImageURI(Uri.fromFile(file));
                } else {
                    ivFoto.setImageResource(R.drawable.mibombo);
                }
            }
        });

        btnGuardar.setOnClickListener(v -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            if (!nuevoNombre.isEmpty()) {
                if (pendingPhotoUri != null) {
                    String localPath = saveImageLocally(pendingPhotoUri);
                    if (localPath != null) {
                        userViewModel.setPhotoUri(localPath);
                    }
                }
                userViewModel.setName(nuevoNombre);
                Toast.makeText(getContext(), getString(R.string.toast_perfil_guardado), Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                etNombre.setError(getString(R.string.invalid_username));
            }
        });

        btnEditarEmail.setOnClickListener(v -> loadFragment(new EditarMailFragment()));
        btnEditarPassword.setOnClickListener(v -> loadFragment(new EditarPasswordFragment()));

        btnEliminarCuenta.setOnClickListener(v -> mostrarDialogoEliminar());

        ivFoto.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        return view;
    }

    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_eliminar_cuenta_titulo)
                .setMessage(R.string.dialog_eliminar_cuenta_mensaje)
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    userViewModel.deleteAccount();
                    Toast.makeText(getContext(), R.string.cuenta_eliminada, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private boolean isImageSizeValid(Uri uri) {
        try {
            Context context = requireContext();
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                long fileSize = inputStream.available();
                inputStream.close();
                // 1 MB = 1024 * 1024 bytes
                return fileSize <= (1024 * 1024);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String saveImageLocally(Uri uri) {
        try {
            Context context = requireContext();
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File file = userViewModel.getUserPhotoFile();
            if (file == null) return null;

            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
