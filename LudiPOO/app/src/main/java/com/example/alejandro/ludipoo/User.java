package com.example.alejandro.ludipoo;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Luis on 02-Apr-17.
 */

public class User implements Serializable, Comparable<User> {
    private String email;
    private String nombres;
    private String nControl;
    private String idGrupo;
    private boolean maestro;

    public User(){

    }
    public User(String email, String nombres, String nControl, boolean maestro, String idGrupo) {
        this.email=email;
        this.nombres = nombres;
        this.nControl = nControl;
        this.setMaestro(maestro);
        this.setIdGrupo(idGrupo);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getnControl() {

        return nControl;
    }

    public void setnControl(String nControl) {
        this.nControl = nControl;
    }

    public String getNombres() {

        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public boolean isMaestro() {
        return maestro;
    }

    public void setMaestro(boolean maestro) {
        this.maestro = maestro;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }

    @Override
    public int compareTo(@NonNull User o) {
        return getNombres().compareTo(o.getNombres());
    }
}
