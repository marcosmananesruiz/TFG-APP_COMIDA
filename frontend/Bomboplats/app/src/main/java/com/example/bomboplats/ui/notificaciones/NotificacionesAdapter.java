package com.example.bomboplats.ui.notificaciones;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import java.util.List;

/**
 * Adaptador para la lista de notificaciones.
 */
public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.NotiViewHolder> {

    private List<String> lista;

    // Constructor
    public NotificacionesAdapter(List<String> lista) {
        this.lista = lista;
    }

    // Actualiza la lista de notificaciones
    public void setLista(List<String> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new NotiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        holder.tvMensaje.setText(lista.get(position));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ViewHolder para la notificación
    static class NotiViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;
        public NotiViewHolder(@NonNull View v) {
            super(v);
            tvMensaje = v.findViewById(R.id.tv_noti_mensaje);
        }
    }
}
