package com.example.bomboplats.ui.general;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bomboplats.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para el carrusel de fotos de un restaurante.
 */
public class FotoCarruselAdapter extends RecyclerView.Adapter<FotoCarruselAdapter.FotoViewHolder> {

    private List<String> listaFotos;
    private String defaultImageUrl;
    private static final String BASE_BUCKET = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/";

    // Constructor que recibe la lista de fotos y la URL de la imagen por defecto
    public FotoCarruselAdapter(List<String> fotos, String defaultImageUrl) {
        this.listaFotos = (fotos != null && !fotos.isEmpty()) ? new ArrayList<>(fotos) : new ArrayList<>();
        this.defaultImageUrl = defaultImageUrl;
        
        // Si no hay fotos, añadimos la de por defecto para que el carrusel no esté vacío
        if (this.listaFotos.isEmpty()) {
            this.listaFotos.add(defaultImageUrl);
        }
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurante_foto, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        String fotoPath = listaFotos.get(position);
        String fotoUrl = defaultImageUrl;

        // Forma la ruta de la imagen con Glide desde S3
        if (fotoPath != null && !fotoPath.isEmpty()) {
            if (fotoPath.startsWith("http")) {
                fotoUrl = fotoPath;
            } else {
                fotoUrl = BASE_BUCKET + fotoPath;
            }
        }

        // Carga la imagen
        Glide.with(holder.itemView.getContext())
                .load(fotoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(defaultImageUrl)
                .into(holder.imgFoto);
    }

    @Override
    public int getItemCount() {
        return listaFotos.size();
    }

    // ViewHolder para la foto
    public static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.img_carrusel);
        }
    }
}
