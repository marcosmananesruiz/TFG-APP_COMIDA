package com.example.bomboplats.data;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.bomboplats.data.model.Bombo;
import com.example.bomboplats.data.model.BomboConCantidad;
import com.example.bomboplats.data.model.EstadoPedido;
import com.example.bomboplats.ui.historial.Pedido;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class EstadoBombosRepository {
    private static EstadoBombosRepository instance;
    private final MutableLiveData<List<EstadoPedido>> pedidosEnEstado = new MutableLiveData<>(new ArrayList<>());
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private static final String PREFIX_ESTADOS = "estados_pedidos_";

    private EstadoBombosRepository() {}

    public static synchronized EstadoBombosRepository getInstance() {
        if (instance == null) {
            instance = new EstadoBombosRepository();
        }
        return instance;
    }

    public LiveData<List<EstadoPedido>> getPedidosEnEstado() {
        return pedidosEnEstado;
    }

    public void cargarDesdeDisco(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String currentEmail = prefs.getString(KEY_CURRENT_USER_EMAIL, "usuario1@test.com");
        String json = prefs.getString(PREFIX_ESTADOS + currentEmail, null);
        
        List<EstadoPedido> lista = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    JSONObject pedidoObj = obj.getJSONObject("pedido");
                    
                    JSONArray itemsArray = pedidoObj.getJSONArray("items");
                    List<BomboConCantidad> items = new ArrayList<>();
                    for (int j = 0; j < itemsArray.length(); j++) {
                        JSONObject itemObj = itemsArray.getJSONObject(j);
                        JSONObject bomboObj = itemObj.getJSONObject("bombo");
                        Bombo b = new Bombo(
                            bomboObj.getString("id"),
                            bomboObj.getString("restauranteId"),
                            bomboObj.getString("nombre"),
                            bomboObj.getString("descripcion"),
                            bomboObj.getString("precio")
                        );
                        items.add(new BomboConCantidad(b, itemObj.getInt("cantidad")));
                    }

                    Pedido p = new Pedido(
                        pedidoObj.getString("id"),
                        pedidoObj.getString("fecha"),
                        items,
                        pedidoObj.getDouble("total"),
                        pedidoObj.getString("direccion")
                    );
                    
                    long hora = obj.optLong("timestampCreacion", System.currentTimeMillis());
                    lista.add(new EstadoPedido(p, obj.getString("estado"), hora));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pedidosEnEstado.postValue(lista);
    }

    public void guardarEnDisco(Context context, List<EstadoPedido> lista) {
        pedidosEnEstado.postValue(new ArrayList<>(lista));
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String currentEmail = prefs.getString(KEY_CURRENT_USER_EMAIL, "usuario1@test.com");
        try {
            JSONArray array = new JSONArray();
            for (EstadoPedido ep : lista) {
                JSONObject obj = new JSONObject();
                obj.put("estado", ep.getEstado());
                obj.put("timestampCreacion", ep.getTimestampCreacion());
                
                JSONObject pedidoObj = new JSONObject();
                pedidoObj.put("id", ep.getPedido().getId());
                pedidoObj.put("fecha", ep.getPedido().getFecha());
                pedidoObj.put("total", ep.getPedido().getTotal());
                pedidoObj.put("direccion", ep.getPedido().getDireccion());
                
                JSONArray itemsArray = new JSONArray();
                for (BomboConCantidad bcc : ep.getPedido().getItems()) {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("cantidad", bcc.getCantidad());
                    JSONObject bomboObj = new JSONObject();
                    bomboObj.put("id", bcc.getBombo().getId());
                    bomboObj.put("restauranteId", bcc.getBombo().getRestauranteId());
                    bomboObj.put("nombre", bcc.getBombo().getNombre());
                    bomboObj.put("descripcion", bcc.getBombo().getDescripcion());
                    bomboObj.put("precio", bcc.getBombo().getPrecio());
                    itemObj.put("bombo", bomboObj);
                    itemsArray.put(itemObj);
                }
                pedidoObj.put("items", itemsArray);
                obj.put("pedido", pedidoObj);
                array.put(obj);
            }
            prefs.edit().putString(PREFIX_ESTADOS + currentEmail, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EstadoPedido> getListaActual() {
        List<EstadoPedido> actual = pedidosEnEstado.getValue();
        return actual != null ? actual : new ArrayList<>();
    }
}
