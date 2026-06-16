package com.example.crudapplication.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int ID_Usuario ;

    @ColumnInfo(name="usuario")
    public String usuario;

    @ColumnInfo(name="constrasena")
    public String contrasena;

    @ColumnInfo(name="nombres")
    public String nombres;

    @ColumnInfo(name="apellidos")
    public String apellidos;

    @ColumnInfo (name="correo")
    public String correo;

    //Constructor

    public Usuario(String usuario,String contrasena, String nombres, String apellidos, String correo){
        this.usuario =usuario;
        this.contrasena=contrasena;
        this.nombres=nombres;
        this.apellidos=apellidos;
        this.correo=correo;
    }
    // get y set


    public int getID_Usuario() {
        return ID_Usuario;
    }

    public void setID_Usuario(int ID_Usuario) {
        this.ID_Usuario = ID_Usuario;
    }

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }


}
