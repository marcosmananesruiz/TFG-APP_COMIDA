package com.example.bomboplats.ui.estadobombos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.BomboConCantidad;
import com.example.bomboplats.data.model.EstadoPedido;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EstadoBombosAdapter extends RecyclerView.Adapter<EstadoBombosAdapter.EstadoViewHolder> {

    private List<EstadoPedido> lista = new ArrayList<>();
    private OnEstadoPedidoClickListener listener;

    public interface OnEstadoPedidoClickListener {
        void onPedidoClick(EstadoPedido estadoPedido);
    }

    public EstadoBombosAdapter(OnEstadoPedidoClickListener listener) {
        this.listener = listener;
    }

    public void setLista(List<EstadoPedido> nuevaLista) {
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
        EstadoPedido item = lista.get(position);
        Context context = holder.itemView.getContext();
        
        // Cargar prefijo de pedido desde recursos
        String prefixId = context.getString(R.string.prefix_pedido_id);
        holder.tvNombre.setText(prefixId + item.getPedido().getId());
        
        StringBuilder sb = new StringBuilder();
        for (BomboConCantidad bcc : item.getPedido().getItems()) {
            sb.append(bcc.getCantidad()).append("x ").append(bcc.getBombo().getNombre()).append(", ");
        }
        String resumen = sb.toString();
        if (resumen.length() > 2) resumen = resumen.substring(0, resumen.length() - 2);
        
        holder.tvDescripcion.setText(resumen);
        
        // Cargar prefijo total desde recursos
        String prefixTotal = context.getString(R.string.label_total);
        holder.tvPrecio.setText(prefixTotal + String.format(Locale.getDefault(), "%.2f€", item.getPedido().getTotal()));
        
        holder.tvCantidad.setVisibility(View.VISIBLE);
        
        // Traducir el estado dinámicamente usando las constantes de EstadoPedido
        String estadoRaw = item.getEstado();
        String estadoTraducido = estadoRaw;

        if (EstadoPedido.ESTADO_ENTREGADO.equalsIgnoreCase(estadoRaw)) {
            estadoTraducido = context.getString(R.string.estado_entregado);
            holder.tvCantidad.setTextColor(Color.parseColor("#4CAF50"));
        } else if (EstadoPedido.ESTADO_CAMINO.equalsIgnoreCase(estadoRaw)) {
            estadoTraducido = context.getString(R.string.estado_de_camino);
            holder.tvCantidad.setTextColor(Color.parseColor("#FF9800"));
        } else if (EstadoPedido.ESTADO_PREPARACION.equalsIgnoreCase(estadoRaw)) {
            estadoTraducido = context.getString(R.string.estado_en_preparacion);
            holder.tvCantidad.setTextColor(Color.GRAY);
        } else {
            holder.tvCantidad.setTextColor(Color.GRAY);
        }
        
        holder.tvCantidad.setText(estadoTraducido);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPedidoClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class EstadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvCantidad;
        public EstadoViewHolder(@NonNull View v) {
            super(v);
            tvNombre = v.findViewById(R.id.tv_nombre_bombo);
            tvDescripcion = v.findViewById(R.id.tv_descripcion_bombo);
            tvPrecio = v.findViewById(R.id.tv_precio_bombo);
            tvCantidad = v.findViewById(R.id.tv_cantidad_bombo);
            if (v.findViewById(R.id.img_bombo) != null) {
                v.findViewById(R.id.img_bombo).setVisibility(View.GONE);
            }
            if (v.findViewById(R.id.btn_fav_bombo) != null) v.findViewById(R.id.btn_fav_bombo).setVisibility(View.GONE);
            if (v.findViewById(R.id.btn_agregar_carrito_rapido) != null) v.findViewById(R.id.btn_agregar_carrito_rapido).setVisibility(View.GONE);
        }
    }
}
