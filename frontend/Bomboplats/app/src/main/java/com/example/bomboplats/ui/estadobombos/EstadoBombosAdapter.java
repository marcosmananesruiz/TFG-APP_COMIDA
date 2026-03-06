package com.example.bomboplats.ui.estadobombos;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.EstadoBombo;
import java.util.ArrayList;
import java.util.List;

public class EstadoBombosAdapter extends RecyclerView.Adapter<EstadoBombosAdapter.EstadoViewHolder> {

    private List<EstadoBombo> lista = new ArrayList<>();

    public void setLista(List<EstadoBombo> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EstadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bombo, parent, false);
        return new EstadoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EstadoViewHolder holder, int position) {
        EstadoBombo item = lista.get(position);
        Bombo b = item.getBomboConCantidad().getBombo();
        
        holder.tvNombre.setText(b.getNombre());
        // En el estado, el campo descripción mostrará el ESTADO actual con color
        holder.tvDescripcion.setText("Estado: " + item.getEstado());
        holder.tvPrecio.setText(b.getPrecio());
        
        holder.tvCantidad.setVisibility(View.VISIBLE);
        holder.tvCantidad.setText("x" + item.getBomboConCantidad().getCantidad());

        // Colorear el estado para que se vea mejor
        if (item.getEstado().equals("Entregado")) {
            holder.tvDescripcion.setTextColor(Color.parseColor("#4CAF50")); // Verde
        } else if (item.getEstado().equals("De camino")) {
            holder.tvDescripcion.setTextColor(Color.parseColor("#FF9800")); // Naranja
        } else {
            holder.tvDescripcion.setTextColor(Color.GRAY);
        }

        int resID = holder.itemView.getContext().getResources().getIdentifier(
                b.getId(), "drawable", holder.itemView.getContext().getPackageName());
        if (resID != 0) holder.imgBombo.setImageResource(resID);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class EstadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvCantidad;
        ImageView imgBombo;
        public EstadoViewHolder(@NonNull View v) {
            super(v);
            tvNombre = v.findViewById(R.id.tv_nombre_bombo);
            tvDescripcion = v.findViewById(R.id.tv_descripcion_bombo);
            tvPrecio = v.findViewById(R.id.tv_precio_bombo);
            tvCantidad = v.findViewById(R.id.tv_cantidad_bombo);
            imgBombo = v.findViewById(R.id.img_bombo);
        }
    }
}
