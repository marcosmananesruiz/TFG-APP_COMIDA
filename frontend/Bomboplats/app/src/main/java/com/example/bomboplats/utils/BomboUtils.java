package com.example.bomboplats.utils;

import java.util.List;

/**
 * Utilidades centralizadas para el manejo de platos (Bombos).
 * Evita la duplicidad de lógica y constantes en los adaptadores y fragmentos.
 */
public class BomboUtils {
    public static final String BASE_BUCKET = "https://bomboplats-imagestorage.s3.us-east-1.amazonaws.com/";
    public static final String DEFAULT_BOMBO_IMAGE = BASE_BUCKET + "platos/default.jpg";

    /**
     * Asegura que el precio tenga el formato correcto con el símbolo €.
     */
    public static String formatPrecio(String precio) {
        if (precio == null || precio.isEmpty()) return "0.00€";
        return precio.contains("€") ? precio : precio + "€";
    }

    /**
     * Construye la URL completa de la imagen desde S3.
     */
    public static String getFotoUrl(List<String> fotos) {
        if (fotos == null || fotos.isEmpty()) return DEFAULT_BOMBO_IMAGE;
        String fotoPath = fotos.get(0);
        if (fotoPath == null || fotoPath.isEmpty()) return DEFAULT_BOMBO_IMAGE;
        return fotoPath.startsWith("http") ? fotoPath : BASE_BUCKET + fotoPath;
    }

    /**
     * Convierte una lista de strings en un bloque de texto con viñetas.
     */
    public static String formatModificaciones(List<String> mods) {
        if (mods == null || mods.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mods.size(); i++) {
            sb.append("- ").append(mods.get(i));
            if (i < mods.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }
}
