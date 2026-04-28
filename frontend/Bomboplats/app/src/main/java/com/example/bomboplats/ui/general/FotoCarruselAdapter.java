package com.example.bomboplats.ui.general;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bomboplats.R;
import java.util.List;

public class FotoCarruselAdapter extends RecyclerView.Adapter<FotoCarruselAdapter.FotoViewHolder> {

    private List<String> listaFotos;
    private static final String BASE_BUCKET = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/";
    private static final String DEFAULT_IMAGE = BASE_BUCKET + "restaurantes/default_0.jpg";

    public FotoCarruselAdapter(List<String> listaFotos) {
        this.listaFotos = listaFotos;
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
        String fotoUrl = DEFAULT_IMAGE;

        if (fotoPath != null && !fotoPath.isEmpty()) {
            if (fotoPath.startsWith("http")) {
                fotoUrl = fotoPath;
            } else {
                fotoUrl = BASE_BUCKET + fotoPath;
            }
        }

        Glide.with(holder.itemView.getContext())
                .load(fotoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(DEFAULT_IMAGE)
                .into(holder.imgFoto);
    }

    @Override
    public int getItemCount() {
        return listaFotos != null ? listaFotos.size() : 0;
    }

    public static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.img_carrusel);
        }
    }
}
