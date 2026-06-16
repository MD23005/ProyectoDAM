package com.example.crudapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.crudapplication.R;
import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Vehiculo;

public class AgregarVehiculoDialog extends DialogFragment {


    public interface OnVehiculoGuardadoListener {
        void onVehiculoGuardado();
    }

    private OnVehiculoGuardadoListener listener;
    private AppDB db;


    private EditText etMarca, etModelo, etAño, etPlaca, etPrecio;
    private ImageView imgFoto;
    private Spinner spinnerTipoVehiculo, spinnerEstado;
    private String rutaFoto = "";


    private final ActivityResultLauncher<String> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && getActivity() != null) {
                    // Mantiene la imagen visible al cerrar
                    getActivity().getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    imgFoto.setImageURI(uri);
                    rutaFoto = uri.toString();
                }
            });


    public AgregarVehiculoDialog(OnVehiculoGuardadoListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.dialog_agregar_vehiculo, container, false);

        db = AppDB.getInstance(requireContext());

        imgFoto = vista.findViewById(R.id.imgFoto);
        etMarca = vista.findViewById(R.id.etMarca);
        etModelo = vista.findViewById(R.id.etModelo);
        etAño = vista.findViewById(R.id.etAño);
        etPlaca = vista.findViewById(R.id.etPlaca);
        etPrecio = vista.findViewById(R.id.etPrecio);
        spinnerTipoVehiculo = vista.findViewById(R.id.spinnerTipoVehiculo);
        spinnerEstado = vista.findViewById(R.id.spinnerEstado);
        Button btnSeleccionarFoto = vista.findViewById(R.id.btnSeleccionarFoto);
        Button btnGuardar = vista.findViewById(R.id.btnGuardar);
        Button btnCancelar = vista.findViewById(R.id.btnCancelar);


        String[] tipos = {"Sedán", "SUV", "Pick-up", "Camión", "Van", "Motocicleta", "Deportivo", "Exotico"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                tipos
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoVehiculo.setAdapter(adapterSpinner);

        String[] estados = {"Disponible", "Alquilado", "En mantenimiento", "No disponible"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                estados
        );
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);


        btnCancelar.setOnClickListener(v -> dismiss());

        btnSeleccionarFoto.setOnClickListener(v -> seleccionarImagenLauncher.launch("image/*"));

        btnGuardar.setOnClickListener(v -> guardarVehiculo());

        return vista;
    }

    private void guardarVehiculo() {
        String marca = etMarca.getText().toString().trim();
        String modelo = etModelo.getText().toString().trim();
        String añoStr = etAño.getText().toString().trim();
        String placa = etPlaca.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String tipo = spinnerTipoVehiculo.getSelectedItem().toString();
        String estado = spinnerEstado.getSelectedItem().toString();

        if (marca.isEmpty() || modelo.isEmpty() || añoStr.isEmpty() || placa.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            Vehiculo nuevo = new Vehiculo(marca, modelo, añoStr, placa, tipo, precioStr, estado, rutaFoto);
            db.vehiculoDAO().crear(nuevo);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Vehículo guardado correctamente", Toast.LENGTH_SHORT).show();

                    if (listener != null) {
                        listener.onVehiculoGuardado();
                    }
                    dismiss();
                });
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}