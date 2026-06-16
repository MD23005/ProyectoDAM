package com.example.crudapplication.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.crudapplication.entities.Usuario;
import com.example.crudapplication.entities.Vehiculo;

@Dao
public interface UsuarioDAO
{
    @Insert
    void crear (Usuario usuario);

    @Update
    void actualizar(Usuario usuario);

    @Delete
    void elmiminar (Usuario usuario);

    @Query("SELECT * FROM Usuario WHERE ID_Usuario = :id")
    Usuario consultarUsuario(int id);
    @Query("SELECT * FROM Usuario WHERE usuario = :usuario AND constrasena = :contrasena LIMIT 1")
    Usuario login(String usuario, String contrasena);

}
