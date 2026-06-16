package com.example.crudapplication.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(
        tableName = "AlquilarVehiculo",
        foreignKeys = {
                @ForeignKey(
                        entity = Vehiculo.class,
                        parentColumns = "ID_Auto",
                        childColumns = "ID_Auto",
                        onDelete = ForeignKey.RESTRICT
                ),
                @ForeignKey(
                        entity = Cliente.class,
                        parentColumns = "ID_Cliente",
                        childColumns = "ID_Cliente",
                        onDelete = ForeignKey.RESTRICT
                )
        },
        indices = {
                @Index(value = "ID_Auto"),
                @Index(value = "ID_Cliente")
        }
)
public class AlquilarVehiculo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int ID_Alquiler;

    @ColumnInfo(name = "ID_Cliente")
    public int ID_Cliente;

    @ColumnInfo(name = "ID_Auto")
    public int ID_Auto;

    @ColumnInfo(name = "Fecha_Inicio")
    public String Fecha_Inicio;

    @ColumnInfo(name = "Fecha_Fin")
    public String Fecha_Fin;

    //Constructor vacío
    public AlquilarVehiculo() {
    }

    //Constructor
    public AlquilarVehiculo(int ID_Alquiler, int ID_Cliente, int ID_Auto, String fecha_Inicio, String fecha_Fin) {
        this.ID_Alquiler = ID_Alquiler;
        this.ID_Cliente = ID_Cliente;
        this.ID_Auto = ID_Auto;
        Fecha_Inicio = fecha_Inicio;
        Fecha_Fin = fecha_Fin;
    }

    //Getters y Setters


    public int getID_Alquiler() {
        return ID_Alquiler;
    }

    public void setID_Alquiler(int ID_Alquiler) {
        this.ID_Alquiler = ID_Alquiler;
    }

    public int getID_Cliente() {
        return ID_Cliente;
    }

    public void setID_Cliente(int ID_Cliente) {
        this.ID_Cliente = ID_Cliente;
    }

    public int getID_Auto() {
        return ID_Auto;
    }

    public void setID_Auto(int ID_Auto) {
        this.ID_Auto = ID_Auto;
    }

    public String getFecha_Inicio() {
        return Fecha_Inicio;
    }

    public void setFecha_Inicio(String fecha_Inicio) {
        Fecha_Inicio = fecha_Inicio;
    }

    public String getFecha_Fin() {
        return Fecha_Fin;
    }

    public void setFecha_Fin(String fecha_Fin) {
        Fecha_Fin = fecha_Fin;
    }
}
