package com.example.crudapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Usuario;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "AlquileresPrefs";
    public static final String KEY_RECORDAR = "recordar_sesion";
    public static final String KEY_USUARIO = "usuario_guardado";
    public static final String KEY_CONTRASENA = "contrasena_guardada";
    public static final String KEY_SESION_ACTIVA = "sesion_activa";
    public static final String KEY_TEMA = "tema";

    AppDB db;
    TextInputEditText inputUsuario, inputPassword;
    CheckBox cbRecordar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema guardado ANTES de inflar la vista
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        super.onCreate(savedInstanceState);

        // Si ya hay sesión activa, saltar directo al MainActivity
        if (prefs.getBoolean(KEY_SESION_ACTIVA, false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        db = AppDB.getInstance(this);

        inputUsuario = findViewById(R.id.inputUsuario);
        inputPassword = findViewById(R.id.inputPassword);
        cbRecordar = findViewById(R.id.cbRecordar);

        // Si antes marcó Recordarme, rellenar campos automáticamente
        if (prefs.getBoolean(KEY_RECORDAR, false)) {
            inputUsuario.setText(prefs.getString(KEY_USUARIO, ""));
            inputPassword.setText(prefs.getString(KEY_CONTRASENA, ""));
            cbRecordar.setChecked(true);
        }

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> inicioSesion());

        Button btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        });
    }

    public void inicioSesion() {
        String usuario = inputUsuario.getText().toString().trim();
        String contrasena = inputPassword.getText().toString().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            Usuario user = db.usuarioDAO().login(usuario, contrasena);

            runOnUiThread(() -> {
                if (user == null) {
                    inputUsuario.setError("Usuario o contraseña incorrectos");
                    inputPassword.setError("Usuario o contraseña incorrectos");
                } else {
                    guardarPreferencias(usuario, contrasena);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            });
        }).start();
    }

    private void guardarPreferencias(String usuario, String contrasena) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (cbRecordar.isChecked()) {
            editor.putBoolean(KEY_RECORDAR, true);
            editor.putString(KEY_USUARIO, usuario);
            editor.putString(KEY_CONTRASENA, contrasena);
            editor.putBoolean(KEY_SESION_ACTIVA, true);
        } else {
            editor.putBoolean(KEY_RECORDAR, false);
            editor.remove(KEY_USUARIO);
            editor.remove(KEY_CONTRASENA);
            editor.putBoolean(KEY_SESION_ACTIVA, false);
        }
        editor.apply();
    }
}
