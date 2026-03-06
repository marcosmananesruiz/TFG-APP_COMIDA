package com.example.bomboplats.ui.notificaciones;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import java.util.List;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.NotiViewHolder> {

    private List<String> lista;

    public NotificacionesAdapter(List<String> lista) {
        this.lista = lista;
    }

    public void setLista(List<String> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new NotiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        holder.tv.setText(lista.get(position));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class NotiViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        public NotiViewHolder(@NonNull View v) {
            super(v);
            tv = v.findViewById(android.R.id.text1);
        }
    }
}
