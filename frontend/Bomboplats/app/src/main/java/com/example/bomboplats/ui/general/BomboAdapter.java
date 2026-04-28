package com.example.bomboplats.ui.general;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import java.util.List;

public class BomboAdapter extends RecyclerView.Adapter<BomboAdapter.BomboViewHolder> {

    private List<Bombo> listaBombos;
    private OnBomboClickListener listener;
    private FavoritosProvider favoritosProvider;
    private static final String BASE_BUCKET = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/";
    private static final String DEFAULT_BOMBO_IMAGE = BASE_BUCKET + "platos/default.jpg";

    public interface OnBomboClickListener {
        void onBomboClick(Bombo bombo);
        void onFavoritoClick(Bombo bombo);
        void onAgregarCarritoClick(Bombo b);
    }

    public BomboAdapter(List<Bombo> listaBombos, OnBomboClickListener listener, FavoritosProvider favoritosProvider) {
        this.listaBombos = listaBombos;
        this.listener = listener;
        this.favoritosProvider = favoritosProvider;
    }

    public void setFilteredList(List<Bombo> filteredList) {
        this.listaBombos = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BomboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bombo, parent, false);
        return new BomboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BomboViewHolder holder, int position) {
        Bombo bombo = listaBombos.get(position);
        holder.tvNombre.setText(bombo.getNombre());
        holder.tvDescripcion.setText(bombo.getDescripcion());
        
        // Asegurar formato de precio con €
        String precioStr = bombo.getPrecio();
        if (precioStr != null && !precioStr.isEmpty()) {
            if (!precioStr.contains("€")) {
                precioStr += "€";
            }
        } else {
            precioStr = "0.00€";
        }
        holder.tvPrecio.setText(precioStr);

        // Aplicar icono y color según si es favorito
        boolean esFav = favoritosProvider != null && favoritosProvider.esFavorito(bombo.getRestauranteId(), bombo.getId());
        if (esFav) {
            holder.btnFav.setImageResource(R.drawable.ic_favorite_filled);
            holder.btnFav.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.favorite_red));
        } else {
            holder.btnFav.setImageResource(R.drawable.ic_heart_unselected);
            holder.btnFav.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_color));
        }

        // Carga de imagen con Glide desde S3
        String fotoUrl = DEFAULT_BOMBO_IMAGE;
        if (bombo.getFotos() != null && !bombo.getFotos().isEmpty()) {
            String fotoPath = bombo.getFotos().get(0);
            if (fotoPath != null && !fotoPath.isEmpty()) {
                if (fotoPath.startsWith("http")) {
                    fotoUrl = fotoPath;
                } else {
                    fotoUrl = BASE_BUCKET + fotoPath;
                }
            }
        }

        Glide.with(holder.itemView.getContext())
                .load(fotoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(DEFAULT_BOMBO_IMAGE)
                .into(holder.imgBombo);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBomboClick(bombo);
        });

        holder.btnFav.setOnClickListener(v -> {
            if (listener != null) listener.onFavoritoClick(bombo);
            notifyItemChanged(position);
        });

        holder.btnMasCarrito.setOnClickListener(v -> {
            if (listener != null) listener.onAgregarCarritoClick(bombo);
        });
    }

    @Override
    public int getItemCount() {
        return listaBombos.size();
    }

    public static class BomboViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvCantidad;
        ImageView imgBombo, btnFav, btnMasCarrito;

        public BomboViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_bombo);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion_bombo);
            tvPrecio = itemView.findViewById(R.id.tv_precio_bombo);
            tvCantidad = itemView.findViewById(R.id.tv_cantidad_bombo);
            imgBombo = itemView.findViewById(R.id.img_bombo);
            btnFav = itemView.findViewById(R.id.btn_fav_bombo);
            btnMasCarrito = itemView.findViewById(R.id.btn_agregar_carrito_rapido);
        }
    }
}
