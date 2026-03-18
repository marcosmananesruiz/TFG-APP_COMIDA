package com.example.bomboplats.ui.carrito;

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
import com.example.bomboplats.data.model.BomboConCantidad;
import java.util.List;
import java.util.Set;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private List<BomboConCantidad> listaCarrito;
    private OnCarritoActionListener listener;
    private Set<String> favoritos;

    public interface OnCarritoActionListener {
        void onRestarClick(String itemKey);
        void onFavoritoClick(String itemKey);
        void onBomboClick(Bombo bombo);
    }

    public CarritoAdapter(List<BomboConCantidad> listaCarrito, OnCarritoActionListener listener) {
        this.listaCarrito = listaCarrito;
        this.listener = listener;
    }

    public void actualizarLista(List<BomboConCantidad> nuevaLista, Set<String> favoritos) {
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
        BomboConCantidad item = listaCarrito.get(position);
        Bombo bombo = item.getBombo();
        // Usamos la clave compuesta "restauranteId:bomboId" para las acciones
        String itemKey = bombo.getRestauranteId() + ":" + bombo.getId();
        
        holder.tvNombre.setText(bombo.getNombre());
        holder.tvDescripcion.setText(bombo.getDescripcion());
        holder.tvPrecio.setText(bombo.getPrecio());
        
        // Configuramos la vista para el modo carrito
        holder.tvCantidad.setVisibility(View.VISIBLE);
        holder.tvCantidad.setText("x" + item.getCantidad());
        
        // Ocultamos el botón de añadir (el +) en el carrito
        holder.btnMasCarrito.setVisibility(View.GONE);
        
        // Mostramos el botón de restar (el -)
        holder.btnRestar.setVisibility(View.VISIBLE);
        holder.btnRestar.setOnClickListener(v -> {
            if (listener != null) listener.onRestarClick(itemKey);
        });

        // Configurar favorito usando la clave compuesta
        if (favoritos != null && favoritos.contains(itemKey)) {
            holder.ivFavorito.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            holder.ivFavorito.setImageResource(R.drawable.ic_heart_unselected);
        }

        holder.ivFavorito.setOnClickListener(v -> {
            if (listener != null) listener.onFavoritoClick(itemKey);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBomboClick(bombo);
        });

        int resID = holder.itemView.getContext().getResources().getIdentifier(
                bombo.getId(), "drawable", holder.itemView.getContext().getPackageName());
        
        if (resID != 0) {
            holder.imgBombo.setImageResource(resID);
        } else {
            holder.imgBombo.setImageResource(R.drawable.ic_launcher_background);
        }
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
