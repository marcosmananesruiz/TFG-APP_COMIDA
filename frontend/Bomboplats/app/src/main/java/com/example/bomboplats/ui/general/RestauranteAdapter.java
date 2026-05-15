package com.example.bomboplats.ui.general;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bomboplats.R;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.Restaurante;
import java.util.List;

public class RestauranteAdapter extends RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder> {

    private List<Restaurante> listaRestaurantes;
    private OnRestauranteClickListener listener;
    private static final String BASE_BUCKET = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/";
    private static final String DEFAULT_RESTAURANTE_IMAGE = BASE_BUCKET + "restaurantes/default_0.jpg";

    public interface OnRestauranteClickListener {
        void onRestauranteClick(Restaurante restaurante);
    }

    public RestauranteAdapter(List<Restaurante> listaRestaurantes, OnRestauranteClickListener listener) {
        this.listaRestaurantes = listaRestaurantes;
        this.listener = listener;
    }

    public void setFilteredList(List<Restaurante> filteredList) {
        this.listaRestaurantes = filteredList;
        notifyDataSetChanged();
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @NonNull
    @Override
    public RestauranteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurante, parent, false);
        return new RestauranteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestauranteViewHolder holder, int position) {
        Restaurante restaurante = listaRestaurantes.get(position);
        holder.tvNombre.setText(restaurante.getNombre());
        holder.tvDireccion.setText(restaurante.getUbicacion());
        holder.tvDescripcion.setText(restaurante.getDescripcion());
        holder.tvEstrellas.setText("⭐ " + restaurante.getValoracion());
        holder.tvPrecio.setText(restaurante.getRangoPrecio());

        // Formatear etiquetas: "- tag" uno debajo de otro
        if (restaurante.getEtiquetas() != null && !restaurante.getEtiquetas().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String tag : restaurante.getEtiquetas()) {
                sb.append("- ").append(tag).append("\n");
            }
            // Quitar el último salto de línea
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            holder.tvEtiquetas.setText(sb.toString());
            holder.tvEtiquetas.setVisibility(View.VISIBLE);
        } else {
            holder.tvEtiquetas.setVisibility(View.GONE);
        }

        String fotoUrl = DEFAULT_RESTAURANTE_IMAGE;
        if (restaurante.getFotos() != null && !restaurante.getFotos().isEmpty()) {
            String fotoPath = restaurante.getFotos().get(0);
            if (fotoPath != null && !fotoPath.isEmpty()) {
                if (fotoPath.startsWith("http")) {
                    fotoUrl = fotoPath;
                } else {
                    fotoUrl = BASE_BUCKET + fotoPath;
                }
            }
        }

        Glide.with(holder.itemView.getContext())
                .load(fotoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(DEFAULT_RESTAURANTE_IMAGE)
                .into(holder.imgRestaurante);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                if (isOnline(v.getContext())) {
                    listener.onRestauranteClick(restaurante);
                } else {
                    Toast.makeText(v.getContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        List<Bombo> bombos = restaurante.getMenu();

        double media = bombos.stream()
                .mapToDouble(bombo -> Double.parseDouble(bombo.getPrecio()))
                .average().orElse(0d);

        if (media < 7.5) {
            holder.tvPrecio.setText("€");
        } else if (media >= 7.5 && media < 22) {
            holder.tvPrecio.setText("€€");
        } else {
            holder.tvPrecio.setText("€€€");
        }
    }

    @Override
    public int getItemCount() {
        return listaRestaurantes.size();
    }

    public static class RestauranteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvDescripcion, tvEstrellas, tvPrecio, tvEtiquetas;
        ImageView imgRestaurante;

        public RestauranteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDireccion = itemView.findViewById(R.id.tv_direccion);
            tvDescripcion = itemView.findViewById(R.id.tv_descripcion);
            tvEstrellas = itemView.findViewById(R.id.tv_estrellas);
            tvPrecio = itemView.findViewById(R.id.tv_precio);
            tvEtiquetas = itemView.findViewById(R.id.tv_etiquetas);
            imgRestaurante = itemView.findViewById(R.id.img_restaurante);
        }
    }
}
