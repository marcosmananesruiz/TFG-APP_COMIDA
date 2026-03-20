package com.example.bomboplats.ui.configuracion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import com.example.bomboplats.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ConfiguracionFragment extends Fragment {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LANGUAGE = "language";

    private boolean isInternalChange = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

        SwitchMaterial switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        RadioGroup rgIdioma = view.findViewById(R.id.rg_idioma);
        RadioButton rbEs = view.findViewById(R.id.rb_es);
        RadioButton rbEn = view.findViewById(R.id.rb_en);

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Modo Oscuro
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        switchDarkMode.setChecked(isDarkMode);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? 
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // 1. Obtener idioma actual (prioridad a AppCompatDelegate)
        String currentLang = AppCompatDelegate.getApplicationLocales().toLanguageTags();
        if (currentLang.isEmpty()) {
            currentLang = prefs.getString(KEY_LANGUAGE, "es");
        }

        // 2. Marcar el RadioButton correcto sin disparar el listener
        isInternalChange = true;
        if (currentLang.startsWith("en")) {
            rbEn.setChecked(true);
        } else {
            rbEs.setChecked(true);
        }
        isInternalChange = false;

        // 3. Listener para cambios de idioma
        rgIdioma.setOnCheckedChangeListener((group, checkedId) -> {
            if (isInternalChange) return;

            String selectedLang = (checkedId == R.id.rb_en) ? "en" : "es";
            
            // Solo actuar si el idioma es realmente diferente
            String currentAppLang = AppCompatDelegate.getApplicationLocales().toLanguageTags();
            if (currentAppLang.isEmpty()) currentAppLang = "es";

            if (!selectedLang.equals(currentAppLang)) {
                // Guardar preferencia
                prefs.edit().putString(KEY_LANGUAGE, selectedLang).commit();
                
                // Aplicar cambio. setApplicationLocales recrea la actividad automáticamente.
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(selectedLang));
            }
        });

        return view;
    }

    public void filtrar(String texto) {}
}
