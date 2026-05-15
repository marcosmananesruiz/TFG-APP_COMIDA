package com.example.bomboplats.ui.misbombos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.ui.carrito.CarritoViewModel;
import com.example.bomboplats.ui.cuenta.UserViewModel;
import com.example.bomboplats.ui.general.BomboAdapter;
import com.example.bomboplats.ui.general.DetalleBomboFragment;
import java.util.ArrayList;
import java.util.List;

public class MisBombosFragment extends Fragment implements BomboAdapter.OnBomboClickListener {

    private RecyclerView recyclerView;
    private BomboAdapter adapter;
    private UserViewModel userViewModel;
    private CarritoViewModel carritoViewModel;
    private FoodRepository foodRepository;
    private TextView tvEmptyFavoritos;

    private List<Bombo> bombosFavoritos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_misbombosfavoritos, container, false);

        recyclerView = view.findViewById(R.id.rv_mis_bombos);
        tvEmptyFavoritos = view.findViewById(R.id.tv_empty_favoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        foodRepository = FoodRepository.getInstance(requireContext());

        // Al observar favoritos, la lista se actualizará automáticamente cuando cambie el usuario en UserViewModel
        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), this::actualizarListaFavoritos);

        return view;
    }

    private void actualizarListaFavoritos(List<Bombo> favoritos) {

        this.bombosFavoritos = favoritos;

        if (favoritos.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyFavoritos.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyFavoritos.setVisibility(View.GONE);
            
            if (adapter == null) {
                adapter = new BomboAdapter(favoritos, this, userViewModel);
            } else {
                adapter.setFilteredList(this.bombosFavoritos);
            }
            // Aseguramos que el adaptador esté siempre vinculado al RecyclerView actual (importante tras popBackStack)
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onBomboClick(Bombo b) {
        DetalleBomboFragment fragment = new DetalleBomboFragment();
        Bundle args = new Bundle();
        args.putString("restauranteId", b.getRestauranteId());
        args.putString("bomboId", b.getId());
        args.putString("nombre", b.getNombre());
        args.putString("precio", b.getPrecio());
        args.putString("desc", b.getDescripcion());
        fragment.setArguments(args);

        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
        }
    }

    @Override
    public void onFavoritoClick(Bombo b) {
        // Pasamos ambos IDs para la nueva lógica de favoritos
        userViewModel.toggleFavorito(b);
    }

    @Override
    public void onAgregarCarritoClick(Bombo b) {
        // Usamos la clave compuesta para el carrito
        carritoViewModel.agregarAlCarrito(b, 1, new ArrayList<>());
        String mensaje = getString(R.string.carrito_item_added, 1, b.getNombre());
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    public void filtrar(String texto) {
        // Implementar filtrado si es necesario para favoritos
        if (adapter == null || this.bombosFavoritos == null) return;

        List<Bombo> filteredBombos = this.bombosFavoritos.stream()
                .filter(bombo -> bombo.getNombre().toLowerCase().contains(texto.toLowerCase()) || bombo.getEtiquetas().contains(texto.toLowerCase()))
                .toList();

        this.adapter.setFilteredList(filteredBombos);
    }


}
