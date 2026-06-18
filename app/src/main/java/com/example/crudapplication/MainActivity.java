package com.example.crudapplication;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.crudapplication.HomeFragment;
import com.example.crudapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    public Menu mainMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // 1. Si es la primera vez que se crea la actividad, cargamos el HomeFragment manualmente
        if (savedInstanceState == null) {
            reemplazarFragmento(new HomeFragment());
            // 2. Seteamos visualmente el ítem de Home como seleccionado
            // (Asegúrate de cambiar 'R.id.nav_home' por el ID real que pusiste en tu menu/bottom_nav_menu.xml)
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        // Tu escucha de clics que ya tenías armada
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_vehiculos) {
                selectedFragment = new VehiculosFragment();
            } else if (itemId == R.id.nav_alquilar) {
                selectedFragment = new AlquilarFragment();
            } else if (itemId == R.id.nav_clientes) {
                selectedFragment = new ClientesFragment();
            }else if (itemId == R.id.nav_usuario) {
                selectedFragment = new UsuarioFragment();
            }


            if (selectedFragment != null) {
                reemplazarFragmento(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void reemplazarFragmento(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}