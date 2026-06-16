package com.example.crudapplication.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.crudapplication.entities.AlquilarVehiculo;

import java.util.List;

@Dao
public interface AlquilerDAO{

    //Función para agregar un alquiler
    @Insert
    void agregarAlquiler(AlquilarVehiculo alquiler);

    @Update
    void actualizarAlquiler(AlquilarVehiculo alquiler);

    @Query("SELECT * FROM AlquilarVehiculo")
    List<AlquilarVehiculo> obtenerTodos();

    @Query("SELECT * FROM AlquilarVehiculo WHERE ID_Alquiler = :id")
    AlquilarVehiculo obtenerPorId(int id);

    @Delete
    void eliminarAlquiler(AlquilarVehiculo alquiler);

    @Query("SELECT * FROM AlquilarVehiculo WHERE ID_Cliente = :id")
    List<AlquilarVehiculo> obtenerPorCliente(int id);

    @Query("SELECT * FROM AlquilarVehiculo WHERE ID_Auto = :id")
    List<AlquilarVehiculo> obtenerPorAuto(int id);
}
