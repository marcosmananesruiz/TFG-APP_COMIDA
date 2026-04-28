package com.example.bomboplats.ui.cuenta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button btnNuevaDireccion;
    private RecyclerView rvDirecciones;
    private TextView tvNoDirecciones;
    private DireccionesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direccionescuenta, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        btnNuevaDireccion = view.findViewById(R.id.btn_ir_nueva_direccion);
        rvDirecciones = view.findViewById(R.id.rv_direcciones);
        tvNoDirecciones = view.findViewById(R.id.tv_no_direcciones);

        rvDirecciones.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DireccionesAdapter(new ArrayList<>(), new DireccionesAdapter.OnDireccionActionListener() {
            @Override
            public void onDeleteClick(Direccion d) {
                userViewModel.removeAddressFromUser(d);
            }

            @Override
            public void onEditClick(Direccion d) {
                loadFragment(NuevaDireccionFragment.newInstance(d));
            }
        }, userViewModel);
        rvDirecciones.setAdapter(adapter);

        btnNuevaDireccion.setOnClickListener(v -> {
            loadFragment(new NuevaDireccionFragment());
        });

        // Observar direcciones del usuario
        userViewModel.getUserAddressesObjects().observe(getViewLifecycleOwner(), userAddresses -> {
            if (userAddresses == null || userAddresses.isEmpty()) {
                rvDirecciones.setVisibility(View.GONE);
                tvNoDirecciones.setVisibility(View.VISIBLE);
            } else {
                rvDirecciones.setVisibility(View.VISIBLE);
                tvNoDirecciones.setVisibility(View.GONE);
                adapter.updateList(userAddresses);
            }
        });

        userViewModel.refreshUserData();

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private static class DireccionesAdapter extends RecyclerView.Adapter<DireccionesAdapter.ViewHolder> {
        private List<Direccion> list;
        private final OnDireccionActionListener listener;
        private final UserViewModel viewModel;

        public interface OnDireccionActionListener {
            void onDeleteClick(Direccion d);
            void onEditClick(Direccion d);
        }

        public DireccionesAdapter(List<Direccion> list, OnDireccionActionListener listener, UserViewModel viewModel) {
            this.list = list;
            this.listener = listener;
            this.viewModel = viewModel;
        }

        public void updateList(List<Direccion> newList) {
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
            Direccion d = list.get(position);
            holder.tvAddress.setText(viewModel.formatDireccion(d));
            
            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(d));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(d));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAddress;
            ImageButton btnEdit, btnDelete;
            ViewHolder(View v) {
                super(v);
                tvAddress = v.findViewById(R.id.tv_item_direccion);
                btnEdit = v.findViewById(R.id.btn_edit_direccion);
                btnDelete = v.findViewById(R.id.btn_delete_direccion);
            }
        }
    }
}
