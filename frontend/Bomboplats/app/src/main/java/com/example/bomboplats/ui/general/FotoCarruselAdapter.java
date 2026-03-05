package com.example.bomboplats.ui.general;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import java.util.List;

public class FotoCarruselAdapter extends RecyclerView.Adapter<FotoCarruselAdapter.FotoViewHolder> {

    private List<String> listaFotos;

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
        String fotoId = listaFotos.get(position);
        int resID = holder.itemView.getContext().getResources().getIdentifier(
                fotoId, "drawable", holder.itemView.getContext().getPackageName());
        
        if (resID != 0) {
            holder.imgFoto.setImageResource(resID);
        } else {
            holder.imgFoto.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return listaFotos.size();
    }

    public static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.img_carrusel);
        }
    }
}
