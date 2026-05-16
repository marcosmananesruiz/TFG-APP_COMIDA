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
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.EstadoPedido;
import com.example.bomboplats.ui.historial.PedidoItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para el RecyclerView de los pedidos en estado.
 */
public class EstadoBombosAdapter extends RecyclerView.Adapter<EstadoBombosAdapter.EstadoViewHolder> {

    private List<EstadoPedido> lista = new ArrayList<>();
    private OnEstadoPedidoClickListener listener;
    private FoodRepository foodRepository;

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
        if (foodRepository == null) {
            foodRepository = FoodRepository.getInstance(parent.getContext());
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bombo, parent, false);
        return new EstadoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EstadoViewHolder holder, int position) {
        EstadoPedido item = lista.get(position);
        Context context = holder.itemView.getContext();
        
        String prefixId = context.getString(R.string.prefix_pedido_id);
        holder.tvNombre.setText(prefixId + item.getPedido().getId());

        // Construimos el resumen de los pedidos para mostraro el un formato más legible
        StringBuilder sb = new StringBuilder();
        if (item.getPedido().getItems() != null) {
            for (PedidoItem pi : item.getPedido().getItems()) {
                String itemKey = pi.getRestauranteId() + ":" + pi.getBomboId();
                Bombo bombo = foodRepository.getBomboPorId(itemKey);
                String nombre = (bombo != null) ? bombo.getNombre() : "Plato";
                sb.append(pi.getCantidad()).append("x ").append(nombre).append(", ");
            }
        }

        // Se elimina el último ", "
        String resumen = sb.toString();
        if (resumen.length() > 2) resumen = resumen.substring(0, resumen.length() - 2);
        holder.tvDescripcion.setText(resumen);

        // Muestra el precio
        String prefixTotal = context.getString(R.string.label_total);
        holder.tvPrecio.setText(prefixTotal + String.format(Locale.getDefault(), "%.2f€", item.getPedido().getTotal()));
        
        holder.tvCantidad.setVisibility(View.VISIBLE);
        
        String estadoRaw = item.getEstado();
        String estadoTraducido = estadoRaw;

        // Se cambia el color del texto según el estado
        if (EstadoPedido.ESTADO_ENTREGADO.equalsIgnoreCase(estadoRaw)) {
            estadoTraducido = context.getString(R.string.estado_entregado);
            holder.tvCantidad.setTextColor(Color.parseColor("#4CAF50"));
        } else if (EstadoPedido.ESTADO_CAMINO.equalsIgnoreCase(estadoRaw)) {
            estadoTraducido = context.getString(R.string.estado_de_camino);
            holder.tvCantidad.setTextColor(Color.parseColor("#FF9800"));
        } else if (EstadoPedido.ESTADO_PREPARACION.equalsIgnoreCase(estadoRaw)) {
            estadoTraducido = context.getString(R.string.estado_en_preparacion);
            holder.tvCantidad.setTextColor(Color.GRAY);
        }

        // Mostrar tiempo estimado de llegada en formato hh:mm
        long arrivalMillis = item.getTimestampEntrega();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(arrivalMillis);
        String arrivalTime = String.format(Locale.getDefault(), "%02d:%02d", 
                cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE));

        // Mostrar estado y tiempo estimado de llegada
        String infoStatus = estadoTraducido + " • Llegada: " + arrivalTime;
        holder.tvCantidad.setText(infoStatus);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPedidoClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Clase interna para el ViewHolder
    static class EstadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvCantidad;
        public EstadoViewHolder(@NonNull View v) {
            super(v);
            tvNombre = v.findViewById(R.id.tv_nombre_bombo);
            tvDescripcion = v.findViewById(R.id.tv_descripcion_bombo);
            tvPrecio = v.findViewById(R.id.tv_precio_bombo);
            tvCantidad = v.findViewById(R.id.tv_cantidad_bombo);
            if (v.findViewById(R.id.img_bombo) != null) v.findViewById(R.id.img_bombo).setVisibility(View.GONE);
            if (v.findViewById(R.id.btn_fav_bombo) != null) v.findViewById(R.id.btn_fav_bombo).setVisibility(View.GONE);
            if (v.findViewById(R.id.btn_agregar_carrito_rapido) != null) v.findViewById(R.id.btn_agregar_carrito_rapido).setVisibility(View.GONE);
        }
    }
}
