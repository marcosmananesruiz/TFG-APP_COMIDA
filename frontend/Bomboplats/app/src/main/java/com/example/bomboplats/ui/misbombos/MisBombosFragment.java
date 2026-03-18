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
        userViewModel.getFavoritos().observe(getViewLifecycleOwner(), idsFavoritos -> {
            actualizarListaFavoritos(idsFavoritos);
        });

        return view;
    }

    private void actualizarListaFavoritos(List<String> idsFavoritos) {
        List<Bombo> todosLosBombos = foodRepository.getBombos();
        List<Bombo> favoritos = new ArrayList<>();
        if (idsFavoritos != null) {
            for (Bombo b : todosLosBombos) {
                // Ahora comparamos con la clave compuesta restauranteId:bomboId
                String key = b.getRestauranteId() + ":" + b.getId();
                if (idsFavoritos.contains(key)) {
                    favoritos.add(b);
                }
            }
        }

        if (favoritos.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyFavoritos.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyFavoritos.setVisibility(View.GONE);
            
            if (adapter == null) {
                adapter = new BomboAdapter(favoritos, this, userViewModel);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.setFilteredList(favoritos);
            }
        }
    }

    @Override
    public void onBomboClick(Bombo b) {
        DetalleBomboFragment fragment = new DetalleBomboFragment();
        Bundle args = new Bundle();
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
        userViewModel.toggleFavorito(b.getRestauranteId(), b.getId());
    }

    @Override
    public void onAgregarCarritoClick(Bombo b) {
        // Usamos la clave compuesta para el carrito
        String itemKey = b.getRestauranteId() + ":" + b.getId();
        carritoViewModel.agregarAlCarrito(itemKey, 1);
        Toast.makeText(getContext(), "¡" + b.getNombre() + " añadido al carrito!", Toast.LENGTH_SHORT).show();
    }

    public void filtrar(String texto) {}
}
