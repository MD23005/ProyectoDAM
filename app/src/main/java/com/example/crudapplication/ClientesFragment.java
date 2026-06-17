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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapplication.adapters.ClienteAdapter;
import com.example.crudapplication.data.AppDB;
import com.example.crudapplication.entities.Cliente;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.widget.SearchView;

public class ClientesFragment extends Fragment {

    RecyclerView recyclerClientes;
    ClienteAdapter adapter;
    AppDB db;

    List<Cliente> lista;
    SearchView searchView;


    public ClientesFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarClientes();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_clientes, container, false);

        db = AppDB.getInstance(requireContext());
        searchView = vista.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String texto) {

                new Thread(() -> {

                    List<Cliente> clientesFiltrados;

                    if (texto.trim().isEmpty()) {

                        clientesFiltrados = db.clienteDAO().getAllClientes();

                    } else {

                        clientesFiltrados =
                                db.clienteDAO().buscarClientes("%" + texto.trim() + "%");
                    }

                    if (getActivity() != null) {

                        getActivity().runOnUiThread(() -> {

                            if (adapter != null) {
                                adapter.actualizarLista(clientesFiltrados);
                            }

                        });
                    }

                }).start();

                return true;
            }
        });
        recyclerClientes = vista.findViewById(R.id.recyclerClientes);
        recyclerClientes.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fabAgregar = vista.findViewById(R.id.fabAgregarCliente);

        fabAgregar.setOnClickListener(v -> {
            AgregarClienteDialog dialogo =
                    new AgregarClienteDialog(() -> cargarClientes());

            dialogo.show(getChildFragmentManager(), "AgregarClienteDialog");
        });

        return vista;
    }

    private void cargarClientes() {

        new Thread(() -> {

            lista = db.clienteDAO().getAllClientes();

            if (getActivity() != null) {

                getActivity().runOnUiThread(() -> {

                    adapter = new ClienteAdapter(
                            lista,
                            requireContext(),

                            // Detalle
                            (cliente) -> {

                                Intent intent = new Intent(requireContext(), DetallesClienteActivity.class);

                                intent.putExtra("cliente", cliente);

                                startActivity(intent);
                            },

                            // Eliminar
                            (cliente, position) -> {

                                // 1. Primero consultamos a la base de datos en segundo plano
                                new Thread(() -> {
                                    // Contamos los alquileres asociados al ID del cliente
                                    int alquileresActivos = db.clienteDAO().contarAlquileresDeCliente(cliente.getID_Cliente());

                                    // 2. Regresamos al hilo principal para decidir qué Alert de confirmación mostrar
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> {

                                            if (alquileresActivos > 0) {
                                                // BLOQUEO: Tiene registros vinculados, no se puede borrar
                                                new AlertDialog.Builder(requireContext())
                                                        .setTitle("No se puede eliminar")
                                                        .setMessage("Este cliente está asociado a " + alquileresActivos + " registro(s) de alquiler. Elimina primero su historial de alquileres para no romper el sistema.")
                                                        .setPositiveButton("Aceptar", null)
                                                        .show();

                                            } else {
                                                // SEGURO: el cliente no está asignado a ningún alquiler
                                                new AlertDialog.Builder(requireContext())
                                                        .setTitle("Eliminar Cliente")
                                                        .setMessage("¿Eliminar a " + cliente.getNombre() + "?") // (Ajusté a getNombre() por si usas getter)
                                                        .setPositiveButton("Eliminar", (dialog, which) -> {

                                                            new Thread(() -> {
                                                                db.clienteDAO().deleteCliente(cliente);

                                                                if (getActivity() != null) {
                                                                    getActivity().runOnUiThread(() -> {
                                                                        lista.remove(position);
                                                                        adapter.notifyItemRemoved(position);

                                                                        Toast.makeText(
                                                                                getContext(),
                                                                                "Cliente eliminado",
                                                                                Toast.LENGTH_SHORT
                                                                        ).show();
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

                            // Editar
                            (cliente) -> {

                                EditarClienteDialog dialogo =
                                        EditarClienteDialog.newInstance(
                                                cliente,
                                                () -> cargarClientes()
                                        );

                                dialogo.show(
                                        getChildFragmentManager(),
                                        "EditarClienteDialog"
                                );
                            }
                    );

                    recyclerClientes.setAdapter(adapter);

                });
            }

        }).start();
    }




}