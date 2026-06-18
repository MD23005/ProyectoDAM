package com.example.crudapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Usuario;
import com.google.android.material.textfield.TextInputEditText;

public class RegistroActivity extends AppCompatActivity {

    AppDB db;

    //recoger datos
    TextInputEditText inputNombres, inputApellidos, inputCorreo;
    TextInputEditText inputUsuario, inputContrasena, inputConfirmar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        db = AppDB.getInstance(this);

        inputNombres = findViewById(R.id.inputNombres);
        inputApellidos = findViewById(R.id.inputApellidos);
        inputCorreo = findViewById(R.id.inputCorreo);
        inputUsuario = findViewById(R.id.inputUsuario);
        inputContrasena = findViewById(R.id.inputContrasena);
        inputConfirmar = findViewById(R.id.inputConfirmarContrasena);

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        //Boton volver login
        Button btnVolverLogin = findViewById(R.id.btnVolverLogin);
        btnVolverLogin.setOnClickListener( v ->  {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registrarUsuario(){
        String nombres = inputNombres.getText().toString().trim();
        String apellidos = inputApellidos.getText().toString().trim();
        String correo = inputCorreo.getText().toString().trim();
        String usuario = inputUsuario.getText().toString().trim();
        String contrasena = inputContrasena.getText().toString().trim();
        String confirmar = inputConfirmar.getText().toString().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this
                    , "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!inputContrasena.getText().toString()
                .equals(inputConfirmar.getText().toString())) {
            inputConfirmar.setError("Las contraseñas no coinciden");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            inputCorreo.setError("Correo no válido");
            return;
        }

        new Thread(() -> {
            Usuario existente = db.usuarioDAO().buscarPorUsuario(usuario);
            Usuario correoExistente = db.usuarioDAO().buscarPorCorreo(correo);

            runOnUiThread(() -> {
                if (existente != null) {
                    inputUsuario.setError("Este usuario ya está registrado");
                    return;
                }

                if (correoExistente != null) {
                    inputCorreo.setError("Este correo ya está registrado");
                    return;
                }


                new Thread(() -> {
                    Usuario nuevo = new Usuario(usuario, contrasena, nombres, apellidos, correo);
                    db.usuarioDAO().crear(nuevo);

                    runOnUiThread(() -> {
                        new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setTitle("¡Registro exitoso!")
                                .setMessage("Tu cuenta ha sido creada correctamente.")
                                .setPositiveButton("Continuar", (dialog, which) -> {
                                    startActivity(new Intent(this, LoginActivity.class));
                                    finish();
                                })
                                .setCancelable(false)
                                .show();
                    });
                }).start();
            });
        }).start();

    }

}
