package com.example.bomboplats.ui.historial;

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
import com.example.bomboplats.data.model.StagedBombo;
import com.example.bomboplats.ui.general.FavoritosProvider;

import java.util.List;

/**
 * Adaptador para el detalle del historial de pedidos.
 */
public class HistorialDetalleAdapter extends RecyclerView.Adapter<HistorialDetalleAdapter.ViewHolder> {

    private List<StagedBombo> items;
    private OnItemClickListener listener;
    private FavoritosProvider favoritosProvider;
    private static final String BASE_BUCKET = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/";
    private static final String DEFAULT_BOMBO_IMAGE = BASE_BUCKET + "platos/default.jpg";

    public interface OnItemClickListener {
        void onBomboClick(StagedBombo stagedBombo);
        void onFavoritoClick(Bombo bombo);
    }

    // Constructor que recibe la lista de platos y el listener
    public HistorialDetalleAdapter(List<StagedBombo> items, OnItemClickListener listener, FavoritosProvider favoritosProvider) {
        this.items = items;
        this.listener = listener;
        this.favoritosProvider = favoritosProvider;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bombo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StagedBombo stagedBombo = items.get(position);
        Bombo bombo = stagedBombo.getBombo();

        holder.tvNombre.setText(bombo.getNombre());
        holder.tvDescripcion.setText(bombo.getDescripcion());
        
        String precioStr = bombo.getPrecio();
        if (precioStr != null && !precioStr.contains("€")) {
            precioStr += "€";
        }
        holder.tvPrecio.setText(precioStr);

        // Mostrar cantidad
        holder.tvCantidad.setVisibility(View.VISIBLE);
        holder.tvCantidad.setText("x" + stagedBombo.getCantidad());

        // Mostrar modificaciones
        List<String> mods = stagedBombo.getModificaciones();
        if (mods != null && !mods.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mods.size(); i++) {
                sb.append("- ").append(mods.get(i));
                if (i < mods.size() - 1) {
                    sb.append("\n");
                }
            }
            holder.tvModificaciones.setText(sb.toString());
            holder.tvModificaciones.setVisibility(View.VISIBLE);
        } else {
            holder.tvModificaciones.setVisibility(View.GONE);
        }

        // Ocultar botones de interacción del carrito
        holder.btnMasCarrito.setVisibility(View.GONE);
        holder.btnRestar.setVisibility(View.GONE);

        // Mostrar/ocultar favorito
        boolean esFav = favoritosProvider != null && favoritosProvider.esFavorito(bombo.getId());
        if (esFav) {
            holder.btnFav.setImageResource(R.drawable.ic_favorite_filled);
            holder.btnFav.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.favorite_red));
        } else {
            holder.btnFav.setImageResource(R.drawable.ic_heart_unselected);
            holder.btnFav.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_color));
        }

        // Forma la ruta de la imagen con Glide desde S3
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

        // Cargar la imagen
        Glide.with(holder.itemView.getContext())
                .load(fotoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(DEFAULT_BOMBO_IMAGE)
                .into(holder.imgBombo);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBomboClick(stagedBombo);
        });

        holder.btnFav.setOnClickListener(v -> {
            if (listener != null) listener.onFavoritoClick(bombo);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder para el bombo
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvModificaciones, tvPrecio, tvCantidad;
        ImageView imgBombo, btnFav, btnMasCarrito;
        View btnRestar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_bombo);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion_bombo);
            tvModificaciones = itemView.findViewById(R.id.tv_modificaciones_bombo);
            tvPrecio = itemView.findViewById(R.id.tv_precio_bombo);
            tvCantidad = itemView.findViewById(R.id.tv_cantidad_bombo);
            imgBombo = itemView.findViewById(R.id.img_bombo);
            btnFav = itemView.findViewById(R.id.btn_fav_bombo);
            btnMasCarrito = itemView.findViewById(R.id.btn_agregar_carrito_rapido);
            btnRestar = itemView.findViewById(R.id.btn_remover_uno);
        }
    }
}
