package com.example.bomboplats.ui.general;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import java.util.List;

/**
 * Adaptador para el carrusel de fotos de un restaurante.
 */
public class RestauranteFotoAdapter extends RecyclerView.Adapter<RestauranteFotoAdapter.FotoViewHolder> {

    private List<Integer> fotos;

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurante_foto, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        holder.imageView.setImageResource(fotos.get(position));
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    // Constructor que recibe la lista de fotos
    static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_carrusel);
        }
    }
}
