package com.example.bomboplats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bomboplats.ui.general.GeneralFragment;

public class GeneralActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, GeneralFragment.newInstance())
                    .commitNow();
        }
    }
}