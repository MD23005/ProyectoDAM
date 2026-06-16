package com.example.crudapplication.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Cliente {

    @PrimaryKey(autoGenerate = true)
    public int ID_Cliente;

    @ColumnInfo(name = "Nombre")
    public String nombre;

    @ColumnInfo(name = "DUI")
    public String dui;

    @ColumnInfo(name = "Telefono")
    public String telefono;

    @ColumnInfo(name = "Correo_Electronico")
    public String correoElectronico;

    //Constructor
    public Cliente(String nombre,
                   String dui,
                   String telefono,
                   String correoElectronico) {

        this.nombre = nombre;
        this.dui = dui;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
    }

    //Getters y Setters

    public int getID_Cliente() {
        return ID_Cliente;
    }

    public void setID_Cliente(int ID_Cliente) {
        this.ID_Cliente = ID_Cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
}