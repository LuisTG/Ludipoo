package com.example.alejandro.ludipoo.grupos;

import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Luis on 02-Apr-17.
 */

public class Grupo implements Serializable{
    private int imagen;
    private String maestro;
    private String idGrupo;
    private String nombre;
    private int horaEntrada, horaSalida;
    private long unidadActual;

    public Grupo(){}
    public Grupo(int imagen, String maestro, String nombre, String idGrupo, int horaEntrada, int horaSalida) {
        this.imagen = imagen;
        this.maestro = maestro;
        this.nombre = nombre;
        this.idGrupo = idGrupo;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        unidadActual = 1;
    }

    public String getMaestro() {
        return maestro;
    }

    public int getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(int horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public int getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(int horaSalida) {
        this.horaSalida = horaSalida;
    }

    public void setMaestro(String maestro) {
        this.maestro = maestro;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }

    public long getUnidadActual() {
        return unidadActual;
    }

    public void setUnidadActual(long unidadActual) {
        this.unidadActual = unidadActual;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public final static int KEY_LENGTH = 8;
    public static String generateKey() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < KEY_LENGTH; i++) {
            Random r = new Random();
            int alphaOrNumber = r.nextInt(100);
            char caracter;
            if (alphaOrNumber < 60) { //Letra
                int mayus = r.nextInt(100);
                int interval = 26, init;
                int index = r.nextInt(26);
                if (mayus < 50) { //Mayúscula
                    init = 65;
                } else { //Minúscula
                    init = 97;
                }
                index += init;
                caracter = (char) index;
            } else {
                int index = r.nextInt(10) + 48;
                caracter = (char) index;
            }
            sb.append(caracter);
        }
        String key = sb.toString();
        return key;
    }
}
