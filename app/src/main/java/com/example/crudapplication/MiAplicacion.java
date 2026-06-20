package com.example.crudapplication;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class MiAplicacion extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs =
                getSharedPreferences("config", MODE_PRIVATE);

        boolean modoOscuro =
                prefs.getBoolean("modo_oscuro", true);

        AppCompatDelegate.setDefaultNightMode(
                modoOscuro
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}