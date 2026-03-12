package com.example.bomboplats.ui.general;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Restaurante;
import java.util.List;

public class RestauranteAdapter extends RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder> {

    private List<Restaurante> listaRestaurantes;
    private OnRestauranteClickListener listener;

    public interface OnRestauranteClickListener {
        void onRestauranteClick(Restaurante restaurante);
    }

    public RestauranteAdapter(List<Restaurante> listaRestaurantes, OnRestauranteClickListener listener) {
        this.listaRestaurantes = listaRestaurantes;
        this.listener = listener;
    }

    public void setFilteredList(List<Restaurante> filteredList) {
        this.listaRestaurantes = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestauranteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurante, parent, false);
        return new RestauranteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestauranteViewHolder holder, int position) {
        Restaurante restaurante = listaRestaurantes.get(position);
        holder.tvNombre.setText(restaurante.getNombre());
        holder.tvDescripcion.setText(restaurante.getDescripcion());
        holder.tvEstrellas.setText("⭐ " + restaurante.getValoracion());
        holder.tvPrecio.setText(restaurante.getRangoPrecio());

        // Usamos la primera foto como imagen principal
        if (restaurante.getFotos() != null && !restaurante.getFotos().isEmpty()) {
            String fotoName = restaurante.getFotos().get(0);
            int resID = holder.itemView.getContext().getResources().getIdentifier(
                    fotoName, "drawable", holder.itemView.getContext().getPackageName());
            
            if (resID != 0) {
                holder.imgRestaurante.setImageResource(resID);
            } else {
                holder.imgRestaurante.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.imgRestaurante.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRestauranteClick(restaurante);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaRestaurantes.size();
    }

    public static class RestauranteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvEstrellas, tvPrecio;
        ImageView imgRestaurante;

        public RestauranteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion);
            tvEstrellas = itemView.findViewById(R.id.tv_estrellas);
            tvPrecio = itemView.findViewById(R.id.tv_precio);
            imgRestaurante = itemView.findViewById(R.id.img_restaurante);
        }
    }
}
