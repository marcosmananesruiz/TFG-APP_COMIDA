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
import com.example.bomboplats.api.Direccion;
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
        adapter = new DireccionesAdapter(new ArrayList<>(), false, new DireccionesAdapter.OnDireccionClickListener() {
            @Override
            public void onActionClick(Direccion d, boolean isSearchMode) {
                if (isSearchMode) {
                    userViewModel.addAddressToUser(d);
                } else {
                    userViewModel.removeAddressFromUser(d);
                }
            }
        }, userViewModel);
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
                    adapter.updateList(addresses, true);
                }
            } else {
                updateViewBasedOnUserAddresses();
            }
        });

        // Observar direcciones del usuario
        userViewModel.getUserAddressesObjects().observe(getViewLifecycleOwner(), userAddresses -> {
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
        List<Direccion> userAddresses = userViewModel.getUserAddressesObjects().getValue();
        tvNoEncontrado.setVisibility(View.GONE);
        if (userAddresses == null || userAddresses.isEmpty()) {
            rvDirecciones.setVisibility(View.GONE);
            tvBuscaEntrega.setVisibility(View.VISIBLE);
        } else {
            tvBuscaEntrega.setVisibility(View.GONE);
            rvDirecciones.setVisibility(View.VISIBLE);
            adapter.updateList(userAddresses, false);
        }
    }

    private static class DireccionesAdapter extends RecyclerView.Adapter<DireccionesAdapter.ViewHolder> {
        private List<Direccion> list;
        private boolean isSearchMode;
        private final OnDireccionClickListener listener;
        private final UserViewModel viewModel;

        public interface OnDireccionClickListener {
            void onActionClick(Direccion d, boolean isSearchMode);
        }

        public DireccionesAdapter(List<Direccion> list, boolean isSearchMode, OnDireccionClickListener listener, UserViewModel viewModel) {
            this.list = list;
            this.isSearchMode = isSearchMode;
            this.listener = listener;
            this.viewModel = viewModel;
        }

        public void updateList(List<Direccion> newList, boolean isSearchMode) {
            this.list = newList;
            this.isSearchMode = isSearchMode;
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
            Direccion d = list.get(position);
            holder.tvAddress.setText(viewModel.formatDireccion(d));
            
            if (isSearchMode) {
                // He cambiado ic_add por ic_add_to_cart que sí existe en tus drawables
                holder.btnAction.setImageResource(R.drawable.ic_add_to_cart); 
                holder.btnAction.setColorFilter(holder.itemView.getContext().getColor(R.color.price_green));
            } else {
                holder.btnAction.setImageResource(R.drawable.ic_close);
                holder.btnAction.setColorFilter(holder.itemView.getContext().getColor(R.color.text_color_secondary));
            }

            holder.btnAction.setOnClickListener(v -> listener.onActionClick(d, isSearchMode));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAddress;
            ImageButton btnAction;
            ViewHolder(View v) {
                super(v);
                tvAddress = v.findViewById(R.id.tv_item_direccion);
                btnAction = v.findViewById(R.id.btn_delete_direccion);
            }
        }
    }
}
