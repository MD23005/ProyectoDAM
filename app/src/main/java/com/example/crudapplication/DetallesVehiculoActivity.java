package com.example.crudapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.crudapplication.entities.Vehiculo;

public class DetallesVehiculoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detalles_vehiculo);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarDetalle);
        setSupportActionBar(toolbar);

        Vehiculo vehiculo = (Vehiculo) getIntent().getSerializableExtra("vehiculo", Vehiculo.class);

        if(vehiculo != null){
            getSupportActionBar().setTitle("Detalle: " + vehiculo.marca);

            //Componentes de UI
            TextView tvID = findViewById(R.id.tvDetalleID);
            TextView tvMarcaModelo = findViewById(R.id.tvDetalleMarcaModelo);
            TextView tvPrecio = findViewById(R.id.tvDetallePrecio);
            TextView tvEstado = findViewById(R.id.tvDetalleEstado);
            TextView tvAño = findViewById(R.id.tvDetalleAño);
            TextView tvPlaca = findViewById(R.id.tvDetallePlaca);
            TextView tvTipo = findViewById(R.id.tvDetalleTipo);
            ImageView ivFoto = findViewById(R.id.ivDetalleFoto);

            //Establecemos los valores de las variables
            tvID.setText(String.valueOf(vehiculo.ID_Auto)); // Convertimos el entero a String
            tvMarcaModelo.setText(vehiculo.marca + " " + vehiculo.modelo);
            tvPrecio.setText("$" + vehiculo.precio);
            tvEstado.setText(vehiculo.estado);
            tvAño.setText(vehiculo.año);
            tvPlaca.setText(vehiculo.placa);
            tvTipo.setText(vehiculo.tipo_vehiculo);

            //cambio de color en la etiqueta de los vehículos segúnsu estado
            if (vehiculo.estado != null && "Disponible".equalsIgnoreCase(vehiculo.estado.trim())) {
                tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#2E7D32")); // Verde oscuro
            } else {
                tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#C62828")); // Rojo oscuro
            }

            //Renderizar la imagen
            if (vehiculo.foto != null && !vehiculo.foto.isEmpty()) {
                android.net.Uri uriFoto = android.net.Uri.parse(vehiculo.foto);
                Glide.with(this)
                        .load(uriFoto)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .centerCrop()
                        .into(ivFoto);
            } else {
                ivFoto.setImageResource(R.drawable.ic_launcher_background);
            }
        }

        toolbar.setNavigationOnClickListener(v -> {
            finish(); // Cierra esta actividad y regresa automáticamente a la anterior
        });
    }
}
