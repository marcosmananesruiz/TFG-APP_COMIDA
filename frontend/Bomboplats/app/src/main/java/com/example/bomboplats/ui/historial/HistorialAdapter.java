package com.example.bomboplats.ui.historial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Bombo;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Adaptador para la lista de pedidos.
 */
public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.PedidoViewHolder> {

    private List<Pedido> listaPedidos;
    private final Set<Integer> expandidos = new HashSet<>();
    private OnPedidoClickListener listener;
    private FoodRepository foodRepository;

    public interface OnPedidoClickListener {
        void onProductosClick(Pedido pedido);
    }

    // Constructor que recibe la lista de pedidos y el listener
    public HistorialAdapter(List<Pedido> listaPedidos, OnPedidoClickListener listener) {
        this.listaPedidos = listaPedidos;
        this.listener = listener;
    }

    // Método para actualizar la lista de pedidos
    public void setPedidos(List<Pedido> pedidos) {
        this.listaPedidos = pedidos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (foodRepository == null) {
            foodRepository = FoodRepository.getInstance(parent.getContext());
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido_padre, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);
        
        String prefix = holder.itemView.getContext().getString(R.string.prefix_pedido_id);
        holder.tvId.setText(prefix + pedido.getId());
        
        holder.tvFecha.setText(pedido.getFecha());
        holder.tvTotal.setText(String.format(Locale.getDefault(), "%.2f€", pedido.getTotal()));
        holder.tvDireccion.setText(pedido.getDireccion());

        // Formatear los platos del historial
        StringBuilder sb = new StringBuilder();
        if (pedido.getItems() != null) {
            for (PedidoItem item : pedido.getItems()) {
                String itemKey = item.getRestauranteId() + ":" + item.getBomboId();
                Bombo bombo = foodRepository.getBomboPorId(itemKey);
                String nombre = (bombo != null) ? bombo.getNombre() : "Plato desconocido";
                
                sb.append("• ").append(item.getCantidad()).append("x ")
                  .append(nombre).append("\n");
            }
        }
        holder.tvProductos.setText(sb.toString().trim());

        // Mostrar/ocultar el detalle del pedido
        boolean isExpanded = expandidos.contains(position);
        holder.layoutDetalle.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivExpand.setRotation(isExpanded ? 180 : 0);

        // Manejar el clic en el pedido
        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            if (expandidos.contains(currentPos)) {
                expandidos.remove(currentPos);
            } else {
                expandidos.add(currentPos);
            }
            notifyItemChanged(currentPos);
        });

        holder.tvProductos.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductosClick(pedido);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos != null ? listaPedidos.size() : 0;
    }

    // ViewHolder para el pedido
    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvFecha, tvTotal, tvProductos, tvDireccion;
        View layoutDetalle;
        ImageView ivExpand;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_pedido_id);
            tvFecha = itemView.findViewById(R.id.tv_pedido_fecha);
            tvTotal = itemView.findViewById(R.id.tv_pedido_total);
            tvProductos = itemView.findViewById(R.id.tv_pedido_productos);
            tvDireccion = itemView.findViewById(R.id.tv_pedido_direccion);
            layoutDetalle = itemView.findViewById(R.id.layout_detalle_pedido);
            ivExpand = itemView.findViewById(R.id.iv_expand);
        }
    }
}
