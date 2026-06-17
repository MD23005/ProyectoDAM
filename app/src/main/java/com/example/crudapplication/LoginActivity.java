package com.example.crudapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Usuario;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    AppDB db;
    TextInputEditText inputUsuario, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = AppDB.getInstance(this);

        inputUsuario = findViewById(R.id.inputUsuario);
        inputPassword = findViewById(R.id.inputPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> inicioSesion());

        //Boton registrar
        Button btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(v ->  {
        Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
        startActivity(intent);
    });
    }

    public void inicioSesion(){
        String usuario = inputUsuario.getText().toString().trim();
        String contrasena = inputPassword.getText().toString().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()){
            Toast.makeText(this
                    , "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            // Buscar usuario que coincida con usuario Y contraseña
            Usuario user = db.usuarioDAO().login(usuario, contrasena);

            runOnUiThread(() -> {
                if (user == null) {
                    // No existe — credenciales incorrectas
                    inputUsuario.setError("Usuario o contraseña incorrectos");
                    inputPassword.setError("Usuario o contraseña incorrectos");
                } else {
                    // Existe — ir al MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            });
        }).start();

    }

}
