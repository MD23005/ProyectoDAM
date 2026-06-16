package com.example.crudapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Cliente;
import com.example.crudapplication.entities.Vehiculo;
import com.example.crudapplication.entities.AlquilarVehiculo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class EditarAlquilerDialog extends DialogFragment {

    private Spinner spinnerClientes, spinnerVehiculos;
    private EditText etFechaInicio, etFechaFin;
    private Button btnGuardar, btnCancelar;
    private TextView tvTituloDialogo;

    private AppDB db;
    private List<Cliente> listaClientes = new ArrayList<>();
    private List<Vehiculo> listaVehiculos = new ArrayList<>();

    private AlquilarVehiculo alquilerEditar;
    private int idVehiculoAnterior = -1;

    public EditarAlquilerDialog() {
        // Constructor vacío obligatorio
    }

    // Método estático para transferir el objeto Alquiler al Diálogo
    public static EditarAlquilerDialog newInstance(AlquilarVehiculo alquiler) {
        EditarAlquilerDialog dialog = new EditarAlquilerDialog();
        Bundle args = new Bundle();
        args.putSerializable("alquiler_objeto", alquiler);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Reutilizamos el mismo layout que AgregarAlquiler
        View view = inflater.inflate(R.layout.dialog_agregar_alquiler, null);

        db = AppDB.getInstance(requireContext());

        // Recuperar el objeto enviado desde el RecyclerView
        if (getArguments() != null) {
            alquilerEditar = (AlquilarVehiculo) getArguments().getSerializable("alquiler_objeto");
            if (alquilerEditar != null) {
                idVehiculoAnterior = alquilerEditar.getID_Auto();
            }
        }

        // Vincular componentes
        tvTituloDialogo = view.findViewById(R.id.tvTituloDialogo);
        spinnerClientes = view.findViewById(R.id.spinnerClientes);
        spinnerVehiculos = view.findViewById(R.id.spinnerVehiculos);
        etFechaInicio = view.findViewById(R.id.etFechaInicio);
        etFechaFin = view.findViewById(R.id.etFechaFin);
        btnGuardar = view.findViewById(R.id.btnGuardarAlquiler);
        btnCancelar = view.findViewById(R.id.btnCancelarAlquiler);

        // Cambiar el texto del título y del botón para que coincida con la acción
        if (tvTituloDialogo != null) {
            tvTituloDialogo.setText("Editar Registro de Alquiler");
        }
        btnGuardar.setText("Actualizar");

        // Rellenar las cajas de texto con los valores actuales
        if (alquilerEditar != null) {
            etFechaInicio.setText(alquilerEditar.getFecha_Inicio());
            etFechaFin.setText(alquilerEditar.getFecha_Fin());
        }

        // Cargar Spinners pasando el ID del auto actual para incluirlo en la lista de disponibles
        cargarDatosSpinners(idVehiculoAnterior);

        configurarDatePicker(etFechaInicio);
        configurarDatePicker(etFechaFin);

        builder.setView(view);

        btnCancelar.setOnClickListener(v -> dismiss());
        btnGuardar.setOnClickListener(v -> actualizarAlquiler());

        return builder.create();
    }

    private void cargarDatosSpinners(int idAutoActual) {
        Executors.newSingleThreadExecutor().execute(() -> {
            listaClientes = db.clienteDAO().getAllClientes();

            // Usamos la Query especial que trae los disponibles más el coche actual del alquiler
            listaVehiculos = db.vehiculoDAO().obtenerDisponiblesYActual(idAutoActual);

            List<String> nombresClientes = new ArrayList<>();
            for (Cliente c : listaClientes) {
                nombresClientes.add(c.getNombre());
            }

            List<String> nombresVehiculos = new ArrayList<>();
            for (Vehiculo v : listaVehiculos) {
                nombresVehiculos.add(v.getMarca() + " " + v.getModelo() + " [" + v.getPlaca() + "]");
            }

            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapterClientes = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, nombresClientes);
                adapterClientes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClientes.setAdapter(adapterClientes);

                ArrayAdapter<String> adapterVehiculos = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, nombresVehiculos);
                adapterVehiculos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerVehiculos.setAdapter(adapterVehiculos);

                // Posicionar los Spinners de manera automática en el registro que le corresponde
                seleccionarItemsActuales();
            });
        });
    }

    private void seleccionarItemsActuales() {
        if (alquilerEditar == null) return;

        for (int i = 0; i < listaClientes.size(); i++) {
            if (listaClientes.get(i).getID_Cliente() == alquilerEditar.getID_Cliente()) {
                spinnerClientes.setSelection(i);
                break;
            }
        }
        for (int i = 0; i < listaVehiculos.size(); i++) {
            if (listaVehiculos.get(i).getID_Auto() == alquilerEditar.getID_Auto()) {
                spinnerVehiculos.setSelection(i);
                break;
            }
        }
    }

    private void actualizarAlquiler() {
        if (spinnerClientes.getSelectedItem() == null || spinnerVehiculos.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Campos vacíos o incorrectos", Toast.LENGTH_SHORT).show();
            return;
        }

        Cliente clienteSeleccionado = listaClientes.get(spinnerClientes.getSelectedItemPosition());
        Vehiculo vehiculoSeleccionado = listaVehiculos.get(spinnerVehiculos.getSelectedItemPosition());
        String fechaInicio = etFechaInicio.getText().toString().trim();
        String fechaFin = etFechaFin.getText().toString().trim();

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(getContext(), "Por favor completa las fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            // Modificamos las propiedades del objeto existente
            alquilerEditar.setID_Cliente(clienteSeleccionado.getID_Cliente());
            alquilerEditar.setID_Auto(vehiculoSeleccionado.getID_Auto());
            alquilerEditar.setFecha_Inicio(fechaInicio);
            alquilerEditar.setFecha_Fin(fechaFin);

            // 1. Guardamos los cambios del alquiler en la BD
            db.alquilerDAO().actualizarAlquiler(alquilerEditar);

            // 2. Comprobar si cambió de coche en la edición para actualizar los dos estados correspondientes
            if (idVehiculoAnterior != vehiculoSeleccionado.getID_Auto()) {
                db.vehiculoDAO().cambiarEstado(idVehiculoAnterior, "Disponible"); // Liberamos el anterior
                db.vehiculoDAO().cambiarEstado(vehiculoSeleccionado.getID_Auto(), "Alquilado"); // Ocupamos el nuevo
            }

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Alquiler actualizado con éxito", Toast.LENGTH_SHORT).show();

                // Notificar al AlquilarFragment utilizando Fragment Result API
                Bundle resultado = new Bundle();
                resultado.putBoolean("alquilerGuardado", true);
                getParentFragmentManager().setFragmentResult("solicitudAlquiler", resultado);

                dismiss();
            });
        });
    }

    private void configurarDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int año = c.get(Calendar.YEAR);
            int mes = c.get(Calendar.MONTH);
            int dia = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String fechaFormateada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                        editText.setText(fechaFormateada);
                    }, año, mes, dia);

            datePickerDialog.show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}