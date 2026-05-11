package com.example.bomboplats.ui.carrito;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.BomboConCantidad;
import com.example.bomboplats.data.model.StagedBombo;

import java.util.List;
import java.util.Set;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private List<StagedBombo> listaCarrito;
    private OnCarritoActionListener listener;
    private Set<Bombo> favoritos;
    private static final String BASE_BUCKET = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/";
    private static final String DEFAULT_BOMBO_IMAGE = BASE_BUCKET + "platos/default.jpg";

    public interface OnCarritoActionListener {
        void onRestarClick(StagedBombo bombo);
        void onFavoritoClick(Bombo bombo);
        void onBomboClick(StagedBombo bombo);
    }

    public CarritoAdapter(List<StagedBombo> listaCarrito, OnCarritoActionListener listener) {
        this.listaCarrito = listaCarrito;
        this.listener = listener;
    }

    public void actualizarLista(List<StagedBombo> nuevaLista, Set<Bombo> favoritos) {
        this.listaCarrito = nuevaLista;
        this.favoritos = favoritos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bombo, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        StagedBombo item = listaCarrito.get(position);
        Bombo bombo = item.getBombo();
        
        holder.tvNombre.setText(bombo.getNombre());
        holder.tvDescripcion.setText(bombo.getDescripcion());
        
        // Añadir € al precio si no lo tiene
        String precio = bombo.getPrecio();
        if (precio != null && !precio.contains("€")) {
            precio += "€";
        }
        holder.tvPrecio.setText(precio);
        
        // Configuramos la vista para el modo carrito
        holder.tvCantidad.setVisibility(View.VISIBLE);
        holder.tvCantidad.setText("x" + item.getCantidad());
        
        // Ocultamos el botón de añadir (el +) en el carrito
        holder.btnMasCarrito.setVisibility(View.GONE);
        
        // Mostramos el botón de restar (el -)
        holder.btnRestar.setVisibility(View.VISIBLE);
        holder.btnRestar.setOnClickListener(v -> {
            if (listener != null) listener.onRestarClick(item);
        });

        // Configurar favorito usando la clave compuesta y aplicando color
        boolean esFav = favoritos != null && favoritos.contains(bombo);
        if (esFav) {
            holder.ivFavorito.setImageResource(R.drawable.ic_favorite_filled);
            holder.ivFavorito.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.favorite_red));
        } else {
            holder.ivFavorito.setImageResource(R.drawable.ic_heart_unselected);
            holder.ivFavorito.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_color));
        }

        holder.ivFavorito.setOnClickListener(v -> {
            if (listener != null) listener.onFavoritoClick(bombo);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBomboClick(item);
        });

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
    }

    @Override
    public int getItemCount() {
        return listaCarrito.size();
    }

    public static class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvCantidad;
        ImageView imgBombo, btnMasCarrito, ivFavorito;
        Button btnRestar;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_bombo);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion_bombo);
            tvPrecio = itemView.findViewById(R.id.tv_precio_bombo);
            tvCantidad = itemView.findViewById(R.id.tv_cantidad_bombo);
            imgBombo = itemView.findViewById(R.id.img_bombo);
            btnRestar = itemView.findViewById(R.id.btn_remover_uno);
            btnMasCarrito = itemView.findViewById(R.id.btn_agregar_carrito_rapido);
            ivFavorito = itemView.findViewById(R.id.btn_fav_bombo);
        }
    }
}
