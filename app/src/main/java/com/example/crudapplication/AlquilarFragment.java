package com.example.crudapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapplication.adapters.AlquilerAdapter;
import com.example.crudapplication.AgregarAlquilerDialog;
import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.AlquilarVehiculo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.crudapplication.dao.AlquilerDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AlquilarFragment extends Fragment {

    private RecyclerView recyclerAlquileres;
    private FloatingActionButton fabAgregarAlquiler;
    private AppDB db;
    private AlquilerAdapter adapter;
    private List<AlquilarVehiculo> lista;



    public AlquilarFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarga los registros de la base de datos de manera asíncrona cada vez que se visualiza la sección
        verificarYLiberarVehiculos();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_alquilar, container, false);

        db = AppDB.getInstance(requireContext());

        recyclerAlquileres = vista.findViewById(R.id.recyclerAlquileres);
        fabAgregarAlquiler = vista.findViewById(R.id.fabAgregarAlquiler);

        // Vinculamos la orientación vertical para las tarjetas de item_alquiler
        recyclerAlquileres.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAgregarAlquiler.setOnClickListener(v -> {
            AgregarAlquilerDialog dialogo = new AgregarAlquilerDialog();
            dialogo.show(getParentFragmentManager(), "AgregarAlquiler");
        });

        return vista;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("solicitudAlquiler", getViewLifecycleOwner(), (requestKey, result) -> {
            boolean guardado = result.getBoolean("alquilerGuardado");
            if (guardado) {
                verificarYLiberarVehiculos();
            }
        });
    }

    private void cargarAlquileres() {
        new Thread(() -> {
            // Consultamos todos los alquileres almacenados en la base de datos
            lista = db.alquilerDAO().obtenerTodos();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Instanciamos el adaptador pasándole la lista y el contexto obligatorio
                    adapter = new AlquilerAdapter(lista, requireContext());

                    // Asignamos el adaptador al RecyclerView de la interfaz
                    recyclerAlquileres.setAdapter(adapter);
                });
            }
        }).start();
    }

    private void verificarYLiberarVehiculos() {
        // 1. Obtener la fecha de hoy en formato AAAA-MM-DD
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaHoy = sdf.format(new Date());

        // 2. Ejecutar la actualización inteligente en segundo plano
        Executors.newSingleThreadExecutor().execute(() -> {

            db.vehiculoDAO().actualizarEstadosVehiculos(fechaHoy);

            // 3. Una vez regularizados los estados en la BD, refrescamos la lista en la UI
            requireActivity().runOnUiThread(() -> {
                cargarAlquileres();
            });
        });
    }
}