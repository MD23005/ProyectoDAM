package com.example.crudapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.crudapplication.data.AppDB;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView tvAutos, tvClientes, tvAlquilados;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_home, container, false);

        tvAutos      = vista.findViewById(R.id.tv_autos_count);
        tvClientes   = vista.findViewById(R.id.tv_clientes_count);
        tvAlquilados = vista.findViewById(R.id.tv_carros_alquilados);

        // Accesos rápidos
        MaterialCardView cardVehiculos  = vista.findViewById(R.id.cardAccesoVehiculos);
        MaterialCardView cardClientes   = vista.findViewById(R.id.cardAccesoClientes);
        MaterialCardView cardAlquileres = vista.findViewById(R.id.cardAccesoAlquileres);

        cardVehiculos.setOnClickListener(v ->
                requireActivity().findViewById(R.id.nav_vehiculos).performClick()
        );
        cardClientes.setOnClickListener(v ->
                requireActivity().findViewById(R.id.nav_clientes).performClick()
        );
        cardAlquileres.setOnClickListener(v ->
                requireActivity().findViewById(R.id.nav_alquilar).performClick()
        );

        cargarDatos();
        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDatos();
    }

    private void cargarDatos() {
        AppDB db = AppDB.getInstance(requireContext());

        Executors.newSingleThreadExecutor().execute(() -> {
            int totalAutos      = db.vehiculoDAO().obtenerTodos().size();
            int totalClientes   = db.clienteDAO().getAllClientes().size();
            int totalAlquilados = db.vehiculoDAO().contarAlquilados();

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (isAdded()) {
                        tvAutos.setText(String.valueOf(totalAutos));
                        tvClientes.setText(String.valueOf(totalClientes));
                        tvAlquilados.setText(String.valueOf(totalAlquilados));
                    }
                });
            }
        });
    }
}