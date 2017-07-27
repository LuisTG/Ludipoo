package com.example.alejandro.ludipoo.unidades;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

/**
 * Created by Luis on 15-May-17.
 */

public class Unidad implements Serializable{

    private String nombre;
    private int id;

    public Unidad(String nombre, int id) {
        this.nombre = nombre;
        this.id = id;
    }

    public Unidad(){
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
