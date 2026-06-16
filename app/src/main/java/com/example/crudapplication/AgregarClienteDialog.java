package com.example.crudapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Cliente;

public class AgregarClienteDialog extends DialogFragment {

    public interface OnClienteGuardadoListener {
        void onClienteGuardado();
    }

    private OnClienteGuardadoListener listener;
    private AppDB db;

    private EditText etNombre;
    private EditText etDui;
    private EditText etTelefono;
    private EditText etCorreo;

    public AgregarClienteDialog(OnClienteGuardadoListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(
                R.layout.dialog_agregar_cliente,
                container,
                false
        );

        db = AppDB.getInstance(requireContext());

        etNombre = vista.findViewById(R.id.etNombre);
        etDui = vista.findViewById(R.id.etDui);
        etTelefono = vista.findViewById(R.id.etTelefono);
        etCorreo = vista.findViewById(R.id.etCorreo);

        Button btnGuardar = vista.findViewById(R.id.btnGuardar);
        Button btnCancelar = vista.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> guardarCliente());

        return vista;
    }

    private void guardarCliente() {

        String nombre = etNombre.getText().toString().trim();
        String dui = etDui.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) ||
                TextUtils.isEmpty(dui) ||
                TextUtils.isEmpty(telefono) ||
                TextUtils.isEmpty(correo)) {

            Toast.makeText(
                    getContext(),
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new Thread(() -> {

            Cliente cliente = new Cliente(
                    nombre,
                    dui,
                    telefono,
                    correo
            );

            db.clienteDAO().insertCliente(cliente);

            if (getActivity() != null) {

                getActivity().runOnUiThread(() -> {

                    Toast.makeText(
                            requireContext(),
                            "Cliente guardado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    if (listener != null) {
                        listener.onClienteGuardado();
                    }

                    dismiss();
                });
            }

        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null &&
                getDialog().getWindow() != null) {

            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}