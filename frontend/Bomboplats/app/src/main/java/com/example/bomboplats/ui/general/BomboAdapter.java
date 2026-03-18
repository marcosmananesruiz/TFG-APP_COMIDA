package com.example.bomboplats.ui.general;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import java.util.List;

public class BomboAdapter extends RecyclerView.Adapter<BomboAdapter.BomboViewHolder> {

    private List<Bombo> listaBombos;
    private OnBomboClickListener listener;
    private FavoritosProvider favoritosProvider;

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
        holder.tvPrecio.setText(bombo.getPrecio());

        // Actualizado: Pasamos tanto restauranteId como bomboId para validar el favorito
        if (favoritosProvider != null && favoritosProvider.esFavorito(bombo.getRestauranteId(), bombo.getId())) {
            holder.btnFav.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            holder.btnFav.setImageResource(R.drawable.ic_heart_unselected);
        }

        int resID = holder.itemView.getContext().getResources().getIdentifier(
                bombo.getId(), "drawable", holder.itemView.getContext().getPackageName());
        
        if (resID != 0) {
            holder.imgBombo.setImageResource(resID);
        } else {
            holder.imgBombo.setImageResource(R.drawable.ic_launcher_background);
        }

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
