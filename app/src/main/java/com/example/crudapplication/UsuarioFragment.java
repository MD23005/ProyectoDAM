package com.example.crudapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class UsuarioFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_usuario, container, false);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);

        // Switch tema oscuro/claro
        Switch switchTema = vista.findViewById(R.id.switchTema);
        int temaActual = prefs.getInt(LoginActivity.KEY_TEMA, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        switchTema.setChecked(temaActual == AppCompatDelegate.MODE_NIGHT_YES);

        switchTema.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int nuevoTema = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            prefs.edit().putInt(LoginActivity.KEY_TEMA, nuevoTema).apply();
            AppCompatDelegate.setDefaultNightMode(nuevoTema);
        });

        // Botón cerrar sesión
        Button btnCerrarSesion = vista.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> {
            prefs.edit()
                    .putBoolean(LoginActivity.KEY_SESION_ACTIVA, false)
                    .putBoolean(LoginActivity.KEY_RECORDAR, false)
                    .remove(LoginActivity.KEY_USUARIO)
                    .remove(LoginActivity.KEY_CONTRASENA)
                    .apply();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return vista;
    }
}