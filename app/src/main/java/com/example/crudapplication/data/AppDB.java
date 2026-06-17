package com.example.crudapplication.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.crudapplication.dao.AlquilerDAO;
import com.example.crudapplication.dao.ClienteDAO;
import com.example.crudapplication.dao.UsuarioDAO;
import com.example.crudapplication.dao.VehiculoDAO;
import com.example.crudapplication.entities.AlquilarVehiculo;
import com.example.crudapplication.entities.Cliente;
import com.example.crudapplication.entities.Usuario;
import com.example.crudapplication.entities.Vehiculo;

@Database(entities ={Vehiculo.class, AlquilarVehiculo.class, Cliente.class, Usuario.class},version = 8)
public abstract class AppDB
        extends RoomDatabase {
    private static AppDB instancia;
    public abstract VehiculoDAO vehiculoDAO();
    public abstract AlquilerDAO alquilerDAO();
    public abstract ClienteDAO clienteDAO();
    public  abstract UsuarioDAO usuarioDAO();

    public static synchronized AppDB getInstance(Context context) {
        if (instancia == null) {
            instancia =
                    Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDB.class,
                                    "db_alquileres"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return instancia;
    }
}
