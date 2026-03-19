package com.example.bomboplats.ui.configuracion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.bomboplats.GeneralActivity;
import com.example.bomboplats.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

public class ConfiguracionFragment extends Fragment {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LANGUAGE = "language";

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
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Cargar Idioma actual (Estado inicial de los RadioButtons)
        String currentLang = prefs.getString(KEY_LANGUAGE, "es");
        if (currentLang.equals("en")) {
            rbEn.setChecked(true);
        } else {
            rbEs.setChecked(true);
        }

        // Listener para cambios manuales
        rgIdioma.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedLang;
            if (checkedId == R.id.rb_en) {
                selectedLang = "en";
            } else {
                selectedLang = "es";
            }
            
            // Solo aplicamos si el idioma seleccionado es diferente al actual guardado
            if (!selectedLang.equals(prefs.getString(KEY_LANGUAGE, "es"))) {
                prefs.edit().putString(KEY_LANGUAGE, selectedLang).apply();
                setLocale(selectedLang);
            }
        });

        return view;
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        requireActivity().getResources().updateConfiguration(config, requireActivity().getResources().getDisplayMetrics());

        // Reiniciar la actividad para aplicar cambios
        Intent intent = new Intent(getActivity(), GeneralActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void filtrar(String texto) {}
}
