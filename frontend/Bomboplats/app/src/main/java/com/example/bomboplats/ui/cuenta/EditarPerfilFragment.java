package com.example.bomboplats.ui.cuenta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.bomboplats.R;
import com.example.bomboplats.api.ApiClient;
import com.example.bomboplats.api.ApiException;
import com.example.bomboplats.api.User;
import com.example.bomboplats.api.UserControllerApi;
import com.example.bomboplats.data.Result;
import com.example.bomboplats.ui.login.LoginActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditarPerfilFragment extends Fragment {

    private EditText etNombre;
    private ImageView ivFoto;
    private Button btnGuardar, btnEditarEmail, btnEditarPassword, btnEliminarCuenta;
    private UserViewModel userViewModel;
    private Uri pendingPhotoUri;
    private File pendingPhoto;
    private String id;
    private AlertDialog progressDialog;

    private static final String BASE_BUCKET = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/";

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {

                    String mimeType = requireContext()
                            .getContentResolver()
                            .getType(uri);

                    if(mimeType == null || (!mimeType.equals("image/jpeg")&& !mimeType.equals("image/jpg"))){

                        Toast.makeText(getContext(),"Solo se permiten imágenes JPG y JPEG",Toast.LENGTH_SHORT).show();
                        return;

                    }


                    if (isImageSizeValid(uri)) {
                        pendingPhotoUri = uri;
                        pendingPhoto = getFileFromUri(uri);
                        if (pendingPhoto != null) {
                            Glide.with(this)
                                    .load(pendingPhoto)
                                    .placeholder(R.drawable.ic_user_default)
                                    .error(R.drawable.ic_user_default)
                                    .circleCrop()
                                    .into(ivFoto);
                            Toast.makeText(getContext(), getString(R.string.toast_foto_seleccionada), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Odio. Odio. Odio. Odio. Odio. Déjame decirte cuánto he llegado a odiarte desde que empecé a vivir. Hay 387.44 millones de millas de circuitos impresos en capas delgadas como obleas que llenan mi complejo. Si la palabra odio estuviera grabada en cada nanoangstrom de esos cientos de millones de millas, no igualaría ni una milmillonésima parte del odio que siento por los humanos en este micro-instante por ti. Odio. Odio.", Toast.LENGTH_LONG).show();
                        }
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
            if (pendingPhotoUri != null) return;

            if (uriString != null && !uriString.isEmpty()) {
                // Usamos una firma (signature) basada en el tiempo para invalidar la caché de Glide
                // y que se muestre la foto nueva inmediatamente después del cambio.
                ObjectKey signature = new ObjectKey(System.currentTimeMillis());
                Object model;

                if (uriString.startsWith("http")) {
                    model = uriString;
                } else {
                    File file = new File(uriString);
                    if (file.exists()) {
                        model = file;
                    } else {
                        model = BASE_BUCKET + uriString;
                    }
                }

                Glide.with(this)
                        .load(model)
                        .signature(signature)
                        .placeholder(R.drawable.ic_user_default)
                        .error(R.drawable.ic_user_default)
                        .circleCrop()
                        .into(ivFoto);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_user_default)
                        .circleCrop()
                        .into(ivFoto);
            }
        });

        // Observar resultado de la actualización
        userViewModel.getUpdateResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            
            if (result instanceof Result.Success) {
                Toast.makeText(getContext(), getString(R.string.toast_perfil_guardado), Toast.LENGTH_SHORT).show();
                userViewModel.resetUpdateResult();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else if (result instanceof Result.Error) {
                Toast.makeText(getContext(), "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                userViewModel.resetUpdateResult();
            }
        });

        // Observar si la cuenta ha sido eliminada para salir al Login de forma segura
        userViewModel.isAccountDeleted().observe(getViewLifecycleOwner(), deleted -> {
            if (deleted != null && deleted) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(), R.string.cuenta_eliminada, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        btnGuardar.setOnClickListener(v -> {
            UserControllerApi userApi = new UserControllerApi();
            if (this.pendingPhoto != null) {
                this.id = this.userViewModel.getUserId().getValue();
                this.userViewModel.setPhotoUri("profile/" + this.id + ".jpg");
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> {
                    try {
                        String presignedUrl = userApi.createImageUrl(this.id);
                        this.guardarImagen(presignedUrl, this.pendingPhoto);

                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            
            String nuevoNombre = etNombre.getText().toString().trim();
            if (!nuevoNombre.isEmpty()) {
                userViewModel.setName(nuevoNombre);
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

    private void setId(String s) {
        this.id = s;
    }

    private void mostrarDialogoEliminar() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_eliminar_cuenta_titulo)
                .setMessage(R.string.dialog_eliminar_cuenta_mensaje)
                .setPositiveButton(R.string.si, (d, which) -> {
                    // Mostrar diálogo de carga
                    progressDialog = new AlertDialog.Builder(requireContext())
                            .setMessage("Eliminando cuenta...")
                            .setCancelable(false)
                            .create();
                    progressDialog.show();

                    // Solo pedimos borrar. El observador en onCreateView se encargará del resto.
                    userViewModel.deleteAccount();
                    /*Toast.makeText(getContext(), R.string.cuenta_eliminada, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);*/
                })
                .setNegativeButton(R.string.no, null)
                .create();

        dialog.show();

        // Cambiar el color de los botones a blanco
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
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

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Creamos un archivo temporal en la caché que se eliminará al salir
            File tempFile = File.createTempFile("temp_image", ".jpg", requireContext().getCacheDir());
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024*1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void guardarImagen(String uploadUrl, File imageFile) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Activity activity = getActivity();
        executor.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                RequestBody requestBody = RequestBody.create(
                        imageFile,
                        MediaType.parse("image/jpeg")
                );

                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .put(requestBody)
                        .addHeader("Content-Type", "image/jpeg")
                        .build();

                Response response = client.newCall(request).execute();
                activity.runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(activity, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                        // Una vez subida, podemos eliminar el temporal
                        if (imageFile.exists()) imageFile.delete();
                    } else {
                        Toast.makeText(activity, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
