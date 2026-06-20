package com.example.crudapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapplication.adapters.VehiculoAdapter;
import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Vehiculo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class VehiculosFragment extends Fragment {

    RecyclerView recyclerVehiculos;
    VehiculoAdapter adapter;
    AppDB db;
    List<Vehiculo> lista;

    public VehiculosFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_vehiculos, container, false);

        db = AppDB.getInstance(requireContext());

        recyclerVehiculos = vista.findViewById(R.id.recyclerVehiculos);
        recyclerVehiculos.setLayoutManager(new LinearLayoutManager(getContext()));

        // SearchView del layout
        SearchView searchView = vista.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) adapter.filtrar(newText);
                return true;
            }
        });

        FloatingActionButton fabAgregar = vista.findViewById(R.id.fabAgregar);
        fabAgregar.setOnClickListener(v -> {
            AgregarVehiculoDialog dialogo = new AgregarVehiculoDialog(() -> cargarVehiculos());
            dialogo.show(getChildFragmentManager(), "AgregarVehiculoDialog");
        });

        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarVehiculos();
    }

    private void cargarVehiculos() {
        new Thread(() -> {
            lista = db.vehiculoDAO().obtenerTodos();

            if (lista == null) {
                lista = new ArrayList<>();
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {

                    if (adapter == null) {
                        adapter = new VehiculoAdapter(
                                lista,
                                requireContext(),

                                (vehiculo) -> {
                                    Intent intent = new Intent(requireContext(), DetallesVehiculoActivity.class);

                                    intent.putExtra("vehiculo", vehiculo);

                                    startActivity(intent);
                                },

                                (vehiculo, position) -> {
                                    new Thread(() -> {
                                        int alquileresActivos = db.vehiculoDAO().contarAlquileresDeVehiculo(vehiculo.getID_Auto());

                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(() -> {
                                                if (alquileresActivos > 0) {
                                                    new AlertDialog.Builder(requireContext())
                                                            .setTitle("No se puede eliminar")
                                                            .setMessage("Este vehículo está asignado a " + alquileresActivos + " registro(s) de alquiler. Debes eliminar primero esos registros para poder borrar el vehículo.")
                                                            .setPositiveButton("Aceptar", null)
                                                            .show();
                                                } else {
                                                    new AlertDialog.Builder(requireContext())
                                                            .setTitle("Eliminar vehículo")
                                                            .setMessage("¿Eliminar " + vehiculo.getMarca() + " " + vehiculo.getModelo() + "?")
                                                            .setPositiveButton("Eliminar", (dialog, which) -> {
                                                                new Thread(() -> {
                                                                    db.vehiculoDAO().eliminar(vehiculo);
                                                                    if (getActivity() != null) {
                                                                        getActivity().runOnUiThread(() -> {
                                                                            adapter.eliminarVehiculo(vehiculo);
                                                                            Toast.makeText(getContext(),
                                                                                    "Vehículo eliminado",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        });
                                                                    }
                                                                }).start();
                                                            })
                                                            .setNegativeButton("Cancelar", null)
                                                            .show();
                                                }
                                            });
                                        }
                                    }).start();
                                },

                                (vehiculo) -> {
                                    EditarVehiculoDialog dialogo = EditarVehiculoDialog.newInstance(
                                            vehiculo,
                                            () -> cargarVehiculos()
                                    );
                                    dialogo.show(getChildFragmentManager(), "EditarVehiculoDialog");
                                }
                        );

                        recyclerVehiculos.setAdapter(adapter);

                    } else {
                        adapter.actualizarLista(lista);
                    }
                });
            }
        }).start();
    }
}