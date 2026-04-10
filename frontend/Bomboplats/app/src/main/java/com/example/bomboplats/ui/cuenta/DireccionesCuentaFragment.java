package com.example.bomboplats.ui.cuenta;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.R;
import java.util.ArrayList;
import java.util.List;

public class DireccionesCuentaFragment extends Fragment {

    private UserViewModel userViewModel;
    private EditText etBuscar;
    private RecyclerView rvDirecciones;
    private TextView tvNoEncontrado;
    private TextView tvBuscaEntrega;
    private DireccionesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direccionescuenta, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        etBuscar = view.findViewById(R.id.et_buscar_direccion);
        rvDirecciones = view.findViewById(R.id.rv_direcciones);
        tvNoEncontrado = view.findViewById(R.id.tv_no_encontrado);
        tvBuscaEntrega = view.findViewById(R.id.tv_busca_entrega);

        rvDirecciones.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DireccionesAdapter(new ArrayList<>(), index -> {
            // Lógica de borrado si fuera necesaria
        });
        rvDirecciones.setAdapter(adapter);

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userViewModel.searchAddresses(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Observar direcciones filtradas (búsqueda)
        userViewModel.getFilteredAddresses().observe(getViewLifecycleOwner(), addresses -> {
            String query = etBuscar.getText().toString().trim();
            if (!query.isEmpty()) {
                tvBuscaEntrega.setVisibility(View.GONE);
                if (addresses.isEmpty()) {
                    rvDirecciones.setVisibility(View.GONE);
                    tvNoEncontrado.setVisibility(View.VISIBLE);
                } else {
                    rvDirecciones.setVisibility(View.VISIBLE);
                    tvNoEncontrado.setVisibility(View.GONE);
                    adapter.updateList(addresses);
                }
            } else {
                updateViewBasedOnUserAddresses();
            }
        });

        // Observar direcciones del usuario
        userViewModel.getAddresses().observe(getViewLifecycleOwner(), userAddresses -> {
            if (etBuscar.getText().toString().trim().isEmpty()) {
                updateViewBasedOnUserAddresses();
            }
        });

        tvNoEncontrado.setOnClickListener(v -> {
            NuevaDireccionFragment fragment = new NuevaDireccionFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        userViewModel.refreshUserData();

        return view;
    }

    private void updateViewBasedOnUserAddresses() {
        List<String> userAddresses = userViewModel.getAddresses().getValue();
        tvNoEncontrado.setVisibility(View.GONE);
        if (userAddresses == null || userAddresses.isEmpty()) {
            rvDirecciones.setVisibility(View.GONE);
            tvBuscaEntrega.setVisibility(View.VISIBLE);
        } else {
            tvBuscaEntrega.setVisibility(View.GONE);
            rvDirecciones.setVisibility(View.VISIBLE);
            adapter.updateList(userAddresses);
        }
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
            holder.btnDelete.setVisibility(View.GONE);
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
