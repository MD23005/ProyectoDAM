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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.crudapplication.R;
import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Cliente; // Ajusta a tus rutas reales
import com.example.crudapplication.entities.Vehiculo;
import com.example.crudapplication.entities.AlquilarVehiculo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AgregarAlquilerDialog extends DialogFragment {

    private Spinner spinnerClientes, spinnerVehiculos;
    private EditText etFechaInicio, etFechaFin;
    private Button btnGuardar, btnCancelar;

    // Instancia de la base de datos y listas de datos reales
    private AppDB db;
    private List<Cliente> listaClientes = new ArrayList<>();
    private List<Vehiculo> listaVehiculos = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_agregar_alquiler, null);

        // Inicializar Base de Datos (Ajusta la inicialización si usas un Singleton diferente)
        db = AppDB.getInstance(requireContext());

        // Vincular componentes
        spinnerClientes = view.findViewById(R.id.spinnerClientes);
        spinnerVehiculos = view.findViewById(R.id.spinnerVehiculos);
        etFechaInicio = view.findViewById(R.id.etFechaInicio);
        etFechaFin = view.findViewById(R.id.etFechaFin);
        btnGuardar = view.findViewById(R.id.btnGuardarAlquiler);
        btnCancelar = view.findViewById(R.id.btnCancelarAlquiler);

        // Cargar los datos desde la BD a los Spinners
        cargarDatosSpinners();

        //Configurar los datePicker
        configurarDatePicker(etFechaInicio);
        configurarDatePicker(etFechaFin);

        builder.setView(view);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> guardarNuevoAlquiler());

        return builder.create();
    }

    private void cargarDatosSpinners() {
        // Ejecutamos en segundo plano para no bloquear la UI
        Executors.newSingleThreadExecutor().execute(() -> {
            // 1. Obtener las listas directamente de tus DAOs
            // (Asegúrate de tener estos métodos de consulta que retornen List<T> en tus interfaces)
            listaClientes = db.clienteDAO().getAllClientes();
            listaVehiculos = db.vehiculoDAO().obtenerVehiculosDisponibles();

            // 2. Crear listas de String con lo que verá el usuario en el select
            List<String> nombresClientes = new ArrayList<>();
            for (Cliente c : listaClientes) {
                // Modifica esto según los atributos de tu entidad Cliente (ej. nombre, apellido)
                nombresClientes.add(c.getNombre());
            }

            List<String> nombresVehiculos = new ArrayList<>();
            for (Vehiculo v : listaVehiculos) {
                // Modifica esto según los atributos de tu entidad Vehiculo (ej. marca, modelo, placa)
                nombresVehiculos.add(v.getMarca() + " " + v.getModelo() + " [" + v.getPlaca() + "]");
            }

            // 3. Regresar al hilo principal para asignar los adaptadores a la vista
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapterClientes = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, nombresClientes);
                adapterClientes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClientes.setAdapter(adapterClientes);

                ArrayAdapter<String> adapterVehiculos = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, nombresVehiculos);
                adapterVehiculos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerVehiculos.setAdapter(adapterVehiculos);
            });
        });
    }

    private void guardarNuevoAlquiler() {
        // Validar que se haya seleccionado algo
        if (spinnerClientes.getSelectedItem() == null || spinnerVehiculos.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Por favor, asegúrate de tener clientes y vehículos registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener la posición seleccionada en el Spinner
        int posCliente = spinnerClientes.getSelectedItemPosition();
        int posVehiculo = spinnerVehiculos.getSelectedItemPosition();

        // Recuperar los objetos reales usando la misma posición de las listas originales
        Cliente clienteSeleccionado = listaClientes.get(posCliente);
        Vehiculo vehiculoSeleccionado = listaVehiculos.get(posVehiculo);

        String fechaInicio = etFechaInicio.getText().toString().trim();
        String fechaFin = etFechaFin.getText().toString().trim();

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(getContext(), "Por favor completa las fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto del nuevo alquiler con las llaves foráneas correspondientes
        AlquilarVehiculo nuevoAlquiler = new AlquilarVehiculo();
        nuevoAlquiler.setID_Cliente(clienteSeleccionado.getID_Cliente()); // Tu llave primaria de Cliente
        nuevoAlquiler.setID_Auto(vehiculoSeleccionado.getID_Auto());       // Tu llave primaria de Vehiculo
        nuevoAlquiler.setFecha_Inicio(fechaInicio);
        nuevoAlquiler.setFecha_Fin(fechaFin);

        // Insertar en la base de datos en segundo plano
        Executors.newSingleThreadExecutor().execute(() -> {
            db.alquilerDAO().agregarAlquiler(nuevoAlquiler);

            db.vehiculoDAO().cambiarEstado(vehiculoSeleccionado.getID_Auto(), "Alquilado");

            // Avisar al usuario en el hilo principal y cerrar diálogo
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Alquiler registrado con éxito", Toast.LENGTH_SHORT).show();

                Bundle resultado = new Bundle();
                resultado.putBoolean("alquilerGuardado", true);
                getParentFragmentManager().setFragmentResult("solicitudAlquiler", resultado);

                dismiss();
            });
        });
    }

    private void configurarDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            // 1. Obtener la fecha actual como punto de partida para el calendario
            final Calendar c = Calendar.getInstance();
            int año = c.get(Calendar.YEAR);
            int mes = c.get(Calendar.MONTH);
            int dia = c.get(Calendar.DAY_OF_MONTH);

            // 2. Crear el diálogo del calendario
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {

                        String fechaFormateada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);

                        // Colocar la fecha en el EditText seleccionado
                        editText.setText(fechaFormateada);
                    }, año, mes, dia);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            // 3. Mostrar el calendario
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