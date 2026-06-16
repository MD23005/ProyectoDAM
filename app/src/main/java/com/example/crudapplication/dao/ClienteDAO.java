package com.example.crudapplication.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.crudapplication.entities.Cliente;

import java.util.List;

@Dao
public interface ClienteDAO {

    @Insert
    void insertCliente(Cliente cliente);

    @Update
    void updateCliente(Cliente cliente);

    @Delete
    void deleteCliente(Cliente cliente);

    @Query("SELECT * FROM Cliente")
    List<Cliente> getAllClientes();

    @Query("SELECT * FROM Cliente WHERE ID_Cliente = :id")
    Cliente getClienteById(int id);

    @Query("SELECT COUNT(*) FROM AlquilarVehiculo WHERE ID_Cliente = :idCliente")
    int contarAlquileresDeCliente(int idCliente);

    @Query("SELECT * FROM Cliente " +
            "WHERE nombre LIKE :texto " +
            "OR dui LIKE :texto " +
            "OR telefono LIKE :texto " +
            "OR Correo_Electronico  LIKE :texto")
    List<Cliente> buscarClientes(String texto);

}