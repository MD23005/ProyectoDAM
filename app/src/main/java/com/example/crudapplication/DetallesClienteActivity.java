package com.example.crudapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.crudapplication.entities.Cliente;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class DetallesClienteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_cliente);

        SharedPreferences prefs =
                getSharedPreferences("config", MODE_PRIVATE);

        boolean modoOscuro =
                prefs.getBoolean("modo_oscuro", true);

        AppCompatDelegate.setDefaultNightMode(
                modoOscuro
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );


        setContentView(R.layout.detalles_cliente);

        Toolbar toolbar = findViewById(R.id.toolbarDetalleCliente);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Cliente cliente = (Cliente) getIntent().getSerializableExtra("cliente", Cliente.class);

        if (cliente != null) {
            getSupportActionBar().setTitle("Detalle: " + cliente.nombre);

            TextView tvID = findViewById(R.id.tvDetalleIDCliente);
            TextView tvNombre = findViewById(R.id.tvDetalleNombreCliente);
            TextView tvDui = findViewById(R.id.tvDetalleDui);
            TextView tvTelefono = findViewById(R.id.tvDetalleTelefono);
            TextView tvCorreo = findViewById(R.id.tvDetalleCorreo);

            tvID.setText(String.valueOf(cliente.ID_Cliente));
            tvNombre.setText(cliente.nombre);
            tvDui.setText(cliente.dui);
            tvTelefono.setText(cliente.telefono);
            tvCorreo.setText(cliente.correoElectronico);
        }

    }
}
