package com.example.crudapplication.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Vehiculo {
    @PrimaryKey(autoGenerate = true)
    public int ID_Auto;

    @ColumnInfo(name = "marca")
    public String marca;
    @ColumnInfo(name = "modelo")
    public String modelo;
    @ColumnInfo(name = "año")
    public String año;
    @ColumnInfo(name = "placa")
    public String placa;
    @ColumnInfo(name = "tipo_vehiculo")
    public String tipo_vehiculo;
    @ColumnInfo(name = "precio")
    public String precio;
    @ColumnInfo(name = "Estado")
    public String estado;
    @ColumnInfo(name = "Foto")
    public String foto;

    //Constructor
    public Vehiculo(String marca, String modelo, String año, String placa,
                    String tipo_vehiculo, String precio, String estado, String foto) {
        this.marca = marca;
        this.modelo = modelo;
        this.año = año;
        this.placa = placa;
        this.tipo_vehiculo = tipo_vehiculo;
        this.precio = precio;
        this.estado = estado;
        this.foto = foto;

    }



    public int getID_Auto() {
        return ID_Auto;
    }

    public void setID_Auto(int ID_Auto) {
        this.ID_Auto = ID_Auto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = año;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipo_vehiculo() {
        return tipo_vehiculo;
    }

    public void setTipo_vehiculo(String tipo_vehiculo) {
        this.tipo_vehiculo = tipo_vehiculo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
