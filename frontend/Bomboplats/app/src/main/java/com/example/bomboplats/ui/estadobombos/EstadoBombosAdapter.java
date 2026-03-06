package com.example.bomboplats.ui.estadobombos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import java.util.ArrayList;
import java.util.List;

public class EstadoBombosAdapter extends RecyclerView.Adapter<EstadoBombosAdapter.ViewHolder> {

    private List<BomboEnEstado> lista = new ArrayList<>();

    public void setLista(List<BomboEnEstado> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bombo_estado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BomboEnEstado item = lista.get(position);
        Bombo bombo = item.getBombo();

        holder.tvNombre.setText(bombo.getNombre());
        holder.tvDesc.setText(bombo.getDescripcion());
        holder.tvEstado.setText(item.getEstado().getTexto());

        // Color según el estado
        switch (item.getEstado()) {
            case PREPARACION:
                holder.tvEstado.setTextColor(0xFFFF9800); // Naranja
                break;
            case ENVIO:
                holder.tvEstado.setTextColor(0xFF2196F3); // Azul
                break;
            case RECIBIDO:
                holder.tvEstado.setTextColor(0xFF4CAF50); // Verde
                break;
        }

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
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBombo;
        TextView tvNombre, tvDesc, tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBombo = itemView.findViewById(R.id.img_bombo_estado);
            tvNombre = itemView.findViewById(R.id.tv_nombre_bombo_estado);
            tvDesc = itemView.findViewById(R.id.tv_desc_bombo_estado);
            tvEstado = itemView.findViewById(R.id.tv_estado_valor);
        }
    }
}
