package com.example.bomboplats.ui.cuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import java.util.ArrayList;
import java.util.List;

public class DireccionesCuentaFragment extends Fragment {

    private UserViewModel userViewModel;
    private EditText etNuevaDireccion;
    private ImageButton btnAdd;
    private RecyclerView rvDirecciones;
    private TextView tvNoDirecciones;
    private DireccionesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direccionescuenta, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        etNuevaDireccion = view.findViewById(R.id.et_nueva_direccion);
        btnAdd = view.findViewById(R.id.btn_add_direccion);
        rvDirecciones = view.findViewById(R.id.rv_direcciones);
        tvNoDirecciones = view.findViewById(R.id.tv_no_direcciones);

        rvDirecciones.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DireccionesAdapter(new ArrayList<>(), index -> {
            userViewModel.removeAddress(index);
            Toast.makeText(getContext(), R.string.toast_direccion_borrada, Toast.LENGTH_SHORT).show();
        });
        rvDirecciones.setAdapter(adapter);

        userViewModel.getAddresses().observe(getViewLifecycleOwner(), addresses -> {
            if (addresses == null || addresses.isEmpty()) {
                rvDirecciones.setVisibility(View.GONE);
                tvNoDirecciones.setVisibility(View.VISIBLE);
                adapter.updateList(new ArrayList<>());
            } else {
                rvDirecciones.setVisibility(View.VISIBLE);
                tvNoDirecciones.setVisibility(View.GONE);
                adapter.updateList(addresses);
            }
        });

        btnAdd.setOnClickListener(v -> {
            String dir = etNuevaDireccion.getText().toString().trim();
            if (!dir.isEmpty()) {
                userViewModel.addAddress(dir);
                etNuevaDireccion.setText("");
                Toast.makeText(getContext(), R.string.toast_direccion_guardada, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.toast_direccion_vacia, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private static class DireccionesAdapter extends RecyclerView.Adapter<DireccionesAdapter.ViewHolder> {
        private List<String> list;
        private final OnDeleteClickListener listener;

        public interface OnDeleteClickListener {
            void onDelete(int index);
        }

        public DireccionesAdapter(List<String> list, OnDeleteClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        public void updateList(List<String> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_direccion, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvAddress.setText(list.get(position));
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAddress;
            ImageButton btnDelete;
            ViewHolder(View v) {
                super(v);
                tvAddress = v.findViewById(R.id.tv_item_direccion);
                btnDelete = v.findViewById(R.id.btn_delete_direccion);
            }
        }
    }
}
