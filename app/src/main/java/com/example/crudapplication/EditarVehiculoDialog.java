package com.example.crudapplication;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Vehiculo;

public class EditarVehiculoDialog extends DialogFragment {

    EditText etMarca, etModelo, etAño, etPlaca, etPrecio;
    Spinner spinnerTipoVehiculo, spinnerEstado;
    ImageView imgFoto;
    Button btnSeleccionarFoto;
    private Button btnGuardar, btnCancelar;
    private TextView tvTituloDialogo;
    AppDB db;
    String rutaFoto = "";
    Vehiculo vehiculo;
    private Runnable onGuardado;

    // Constructor con el vehículo y el callback
    public static EditarVehiculoDialog newInstance(Vehiculo vehiculo, Runnable onGuardado) {
        EditarVehiculoDialog dialog = new EditarVehiculoDialog();
        dialog.vehiculo = vehiculo;
        dialog.onGuardado = onGuardado;
        return dialog;
    }

    ActivityResultLauncher<String> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    requireActivity().getContentResolver().takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    imgFoto.setImageURI(uri);
                    rutaFoto = uri.toString();
                }
            });

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View vista = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_agregar_vehiculo, null);

        db = AppDB.getInstance(requireContext());

        // Vincular vistas
        tvTituloDialogo = vista.findViewById(R.id.tvTituloDialogo);
        imgFoto = vista.findViewById(R.id.imgFoto);
        etMarca = vista.findViewById(R.id.etMarca);
        etModelo = vista.findViewById(R.id.etModelo);
        etAño = vista.findViewById(R.id.etAño);
        etPlaca = vista.findViewById(R.id.etPlaca);
        etPrecio = vista.findViewById(R.id.etPrecio);
        btnSeleccionarFoto = vista.findViewById(R.id.btnSeleccionarFoto);
        spinnerTipoVehiculo = vista.findViewById(R.id.spinnerTipoVehiculo);
        spinnerEstado = vista.findViewById(R.id.spinnerEstado);
        btnGuardar = vista.findViewById(R.id.btnGuardar);

        // Cambiar el texto del título y del botón para que coincida con la acción
        if (tvTituloDialogo != null) {
            tvTituloDialogo.setText("Editar Vehículo");
        }
        btnGuardar.setText("Actualizar");

        String[] tipos = {"Sedán", "SUV", "Pick-up", "Camión", "Van", "Motocicleta", "Deportivo", "Exotico"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(requireContext(),
                R.layout.spinner_item, tipos);
        adapterTipo.setDropDownViewResource(R.layout.spinner_item);
        spinnerTipoVehiculo.setAdapter(adapterTipo);

        String[] estados = {"Disponible", "Alquilado", "En mantenimiento", "No disponible"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(requireContext(),
                R.layout.spinner_item, estados);
        adapterEstado.setDropDownViewResource(R.layout.spinner_item);
        spinnerEstado.setAdapter(adapterEstado);

        cargarDatos(tipos, estados);

        btnSeleccionarFoto.setOnClickListener(v ->
                seleccionarImagenLauncher.launch("image/*")
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(vista);

        AlertDialog dialog = builder.create();;

        Button btnGuardar = vista.findViewById(R.id.btnGuardar);
        Button btnCancelar = vista.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());
        btnGuardar.setOnClickListener(v -> guardarCambios(dialog));

        return dialog;
    }

    private void cargarDatos(String[] tipos, String[] estados) {
        etMarca.setText(vehiculo.marca);
        etModelo.setText(vehiculo.modelo);
        etAño.setText(vehiculo.año);
        etPlaca.setText(vehiculo.placa);
        etPrecio.setText(vehiculo.precio);
        rutaFoto = vehiculo.foto != null ? vehiculo.foto : "";

        if (!rutaFoto.isEmpty()) {
            Glide.with(this).load(Uri.parse(rutaFoto)).centerCrop().into(imgFoto);
        }

        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].equals(vehiculo.tipo_vehiculo)) {
                spinnerTipoVehiculo.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < estados.length; i++) {
            if (estados[i].equals(vehiculo.estado)) {
                spinnerEstado.setSelection(i);
                break;
            }
        }
    }

    private void guardarCambios(AlertDialog dialog) {
        vehiculo.marca = etMarca.getText().toString().trim();
        vehiculo.modelo = etModelo.getText().toString().trim();
        vehiculo.año = etAño.getText().toString().trim();
        vehiculo.placa = etPlaca.getText().toString().trim();
        vehiculo.tipo_vehiculo = spinnerTipoVehiculo.getSelectedItem().toString();
        vehiculo.precio = etPrecio.getText().toString().trim();
        vehiculo.estado = spinnerEstado.getSelectedItem().toString();
        vehiculo.foto = rutaFoto;

        if (vehiculo.marca.isEmpty() || vehiculo.modelo.isEmpty() ||
                vehiculo.año.isEmpty() || vehiculo.placa.isEmpty() || vehiculo.precio.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            db.vehiculoDAO().actualizar(vehiculo);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Vehículo actualizado", Toast.LENGTH_SHORT).show();
                    if (onGuardado != null) onGuardado.run(); // refresca la lista
                    dialog.dismiss();
                });
            }
        }).start();
    }
}