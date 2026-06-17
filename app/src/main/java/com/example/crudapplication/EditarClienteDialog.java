package com.example.crudapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Cliente;

public class EditarClienteDialog extends DialogFragment {

    EditText etNombre;
    EditText etDui;
    EditText etTelefono;
    EditText etCorreo;

    AppDB db;
    Cliente cliente;
    private Runnable onGuardado;

    public static EditarClienteDialog newInstance(
            Cliente cliente,
            Runnable onGuardado
    ) {
        EditarClienteDialog dialog = new EditarClienteDialog();
        dialog.cliente = cliente;
        dialog.onGuardado = onGuardado;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View vista = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_editar_cliente, null);

        db = AppDB.getInstance(requireContext());

        etNombre = vista.findViewById(R.id.etNombre);
        etDui = vista.findViewById(R.id.etDui);
        etTelefono = vista.findViewById(R.id.etTelefono);
        etCorreo = vista.findViewById(R.id.etCorreo);

        cargarDatos();

        AlertDialog.Builder builder =
                new AlertDialog.Builder(requireContext());

        builder.setView(vista);

        AlertDialog dialog = builder.create();

        Button btnActualizar = vista.findViewById(R.id.btnActualizar);
        Button btnCancelar = vista.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnActualizar.setOnClickListener(v ->
                guardarCambios(dialog));

        return dialog;
    }

    private void cargarDatos() {

        etNombre.setText(cliente.nombre);
        etDui.setText(cliente.dui);
        etTelefono.setText(cliente.telefono);
        etCorreo.setText(cliente.correoElectronico);
    }

    private void guardarCambios(AlertDialog dialog) {

        cliente.nombre = etNombre.getText().toString().trim();
        cliente.dui = etDui.getText().toString().trim();
        cliente.telefono = etTelefono.getText().toString().trim();
        cliente.correoElectronico = etCorreo.getText().toString().trim();

        if (cliente.nombre.isEmpty()
                || cliente.dui.isEmpty()
                || cliente.telefono.isEmpty()
                || cliente.correoElectronico.isEmpty()) {

            Toast.makeText(
                    getContext(),
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new Thread(() -> {

            db.clienteDAO().updateCliente(cliente);

            if (getActivity() != null) {

                getActivity().runOnUiThread(() -> {

                    Toast.makeText(
                            getContext(),
                            "Cliente actualizado",
                            Toast.LENGTH_SHORT
                    ).show();

                    if (onGuardado != null) {
                        onGuardado.run();
                    }

                    dialog.dismiss();
                });
            }

        }).start();
    }
}