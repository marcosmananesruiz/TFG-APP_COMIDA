package com.example.bomboplats.ui.general;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import java.util.List;

public class BomboAdapter extends RecyclerView.Adapter<BomboAdapter.BomboViewHolder> {

    private List<Bombo> listaBombos;

    public BomboAdapter(List<Bombo> listaBombos) {
        this.listaBombos = listaBombos;
    }

    // Método para actualizar la lista filtrada
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
        holder.tvDescripcion.setText(bombo.getDescripcion()); // Corregido: getDescripcion()
        holder.tvPrecio.setText(bombo.getPrecio());

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
        return listaBombos.size();
    }

    public static class BomboViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio;
        ImageView imgBombo;

        public BomboViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_bombo);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion_bombo);
            tvPrecio = itemView.findViewById(R.id.tv_precio_bombo);
            imgBombo = itemView.findViewById(R.id.img_bombo);
        }
    }
}
