package com.example.bomboplats.ui.general;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bomboplats.R;

import java.util.List;

public class ModificacionesAdapter extends RecyclerView.Adapter<ModificacionesAdapter.ModificacionesViewHolder> {

    private List<String> listaModificaciones;
    private OnModificacionClickListener listener;

    public ModificacionesAdapter(List<String> listaModificaciones, OnModificacionClickListener listener) {
        this.listaModificaciones = listaModificaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModificacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.modificaciones_adapter, null);
        return new ModificacionesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModificacionesViewHolder holder, int position) {
        String modificacion = listaModificaciones.get(position);
        holder.checkBox.setText(modificacion);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onModificacionClick(modificacion, isChecked);
        });
    }

    public interface OnModificacionClickListener {
        void onModificacionClick(String modificacion, boolean isChecked);
    }

    @Override
    public int getItemCount() {
        return listaModificaciones.size();
    }

    public static class ModificacionesViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;

        public ModificacionesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
