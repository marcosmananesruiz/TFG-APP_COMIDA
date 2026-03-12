package com.example.bomboplats.ui.general;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.example.bomboplats.data.FoodRepository;
import com.example.bomboplats.data.model.Restaurante;

import java.util.ArrayList;
import java.util.List;

public class GeneralFragment extends Fragment implements RestauranteAdapter.OnRestauranteClickListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyError;
    private RestauranteAdapter adapter;
    private List<Restaurante> listaCompleta;
    private FoodRepository foodRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        recyclerView = view.findViewById(R.id.rv_restaurantes);
        tvEmptyError = view.findViewById(R.id.tv_empty_error);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        foodRepository = FoodRepository.getInstance(requireContext());
        listaCompleta = foodRepository.getRestaurantes();

        if (listaCompleta == null || listaCompleta.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyError.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyError.setVisibility(View.GONE);
            adapter = new RestauranteAdapter(new ArrayList<>(listaCompleta), this);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onRestauranteClick(Restaurante restaurante) {
        BombosFragment fragment = new BombosFragment();
        Bundle args = new Bundle();
        args.putString("restauranteId", restaurante.getId());
        args.putString("nombre", restaurante.getNombre());
        args.putString("ubicacion", restaurante.getUbicacion());
        args.putString("descripcion", restaurante.getDescripcion());
        // Pasamos las fotos como un ArrayList de Strings
        args.putStringArrayList("fotos", new ArrayList<>(restaurante.getFotos()));
        fragment.setArguments(args);

        if (getActivity() instanceof GeneralActivity) {
            ((GeneralActivity) getActivity()).onRestauranteClickFromFragment(fragment);
        }
    }

    public void filtrar(String texto) {
        if (listaCompleta == null || adapter == null) return;
        List<Restaurante> filtrados = new ArrayList<>();
        String query = texto.toLowerCase().trim();
        if (query.isEmpty()) {
            filtrados.addAll(listaCompleta);
        } else {
            for (Restaurante r : listaCompleta) {
                boolean matchEtiqueta = false;
                if (r.getEtiquetas() != null) {
                    for (String tag : r.getEtiquetas()) {
                        if (tag.toLowerCase().contains(query)) {
                            matchEtiqueta = true;
                            break;
                        }
                    }
                }

                if (r.getNombre().toLowerCase().contains(query) || matchEtiqueta) {
                    filtrados.add(r);
                }
            }
        }
        if (filtrados.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyError.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyError.setVisibility(View.GONE);
            adapter.setFilteredList(filtrados);
        }
    }
}
